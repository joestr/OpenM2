/*
 * Class: ObjectPathNode.java
 */

// package:
package ibs.bo.path;

// imports:
import ibs.BaseObject;
import ibs.bo.BOConstants;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.ml.MultilangConstants;
import ibs.ml.MultilingualTextInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.sql.DBConnector;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;

import java.util.Enumeration;
import java.util.Vector;


/******************************************************************************
 * This class represents an node in the business object path. <BR/>
 *
 * @version     $Id: ObjectPathNode.java,v 1.15 2012/01/03 11:32:12 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ) 020117
 ******************************************************************************
 */
public class ObjectPathNode extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ObjectPathNode.java,v 1.15 2012/01/03 11:32:12 rburgermann Exp $";


    /**
     * node type if current ObjectPathNode is root of object tree. <BR/>
     */
    public static final int TYPE_ROOT = 1;

    /**
     * node type if current ObjectPathNode is node of object tree. <BR/>
     */
    public static final int TYPE_NODE = 2;

    /**
     * node type if current ObjectPathNode is leaf of object tree. <BR/>
     */
    public static final int TYPE_LEAF = 3;

    /**
     * Type of node. <BR/>
     * Must be one of {@link #TYPE_ROOT}, {@link #TYPE_NODE TYPE_NODE},
     * {@link #TYPE_LEAF TYPE_LEAF}. <BR/>
     */
    protected int p_nodeType = 0;

    /**
     * indicates if node is part of parentnode. (tabs). <BR/>
     */
    protected boolean p_partOfParent = false;

    /**
     * oid of object represented by node. <BR/>
     */
    protected OID p_oid = null;

    /**
     * name of object represented by node. <BR/>
     */
    protected String p_name = null;

    /**
     * true if current object is workspaceContainer. <BR/>
     */
    protected boolean p_isWorkspaceContainer = false;

    /**
     * Parent node of tree node. <BR/>
     */
    protected ObjectPathNode p_parent = null;

    /**
     * Oid of parent node. <BR/>
     */
    private OID p_parentOid = null;

    /**
     * Child nodes of tree node. <BR/>
     */
    protected Vector<ObjectPathNode> p_childs = new Vector<ObjectPathNode> ();

    /**
     * The extId of object represented by node. <BR/>
     */
    protected String p_extId = null;

    /**
     * The extIdDomain of object represented by node. <BR/>
     */
    protected String p_extIdDomain = null;

    /**
     * The tabCode of object represented by node. <BR/>
     */
    protected String p_tabCode = null;

    /**************************************************************************
     * This constructor creates a new instance of the class ObjectTreeHandler .
     * <BR/>
     *
     * @param   oid     The oid of the node object.
     */
    public ObjectPathNode (OID oid)
    {
        this.p_oid = oid;
    } // ObjectTreeHandler


    /*
     * GETTERS
     */
    /**************************************************************************
     * Get the type of the current node. <BR/>
     *
     * @return  The node type.
     */
    public int getNodeType ()
    {
        return this.p_nodeType;
    } // getnodeType


    /**************************************************************************
     * Get the oid. <BR/>
     *
     * @return  The oid.
     */
    public OID getOid ()
    {
        return this.p_oid;
    } // getOid


    /**************************************************************************
     * Get the node name. <BR/>
     *
     * @return  The name.
     */
    public String getName ()
    {
        return this.p_name;
    } // getName
    
    
    /**************************************************************************
     * Get the extId. <BR/>
     *
     * @return  The extId.
     */
    public String getExtId ()
    {
        return this.p_extId;
    } // getExtId
    
    
    /**************************************************************************
     * Get the extIdDomain. <BR/>
     *
     * @return  The extIdDomain.
     */
    public String getExtIdDomain ()
    {
        return this.p_extIdDomain;
    } // getExtIdDomain

    
    /**************************************************************************
     * Get the tabCode. <BR/>
     *
     * @return  The tabCode.
     */
    public String getTabCode ()
    {
        return this.p_tabCode;
    } // getTabCode
    
    
    /**************************************************************************
     * Get the node's multilang name. <BR/>
     *
     * @return  The multilang name.
     */
    public String getMlName (Environment env)
    {
        // set fallback name for the name
        String mlName = this.getName (); 
        
        // do we have an ext key to get the multilingual object name
        if (this.p_extId != null && this.p_extIdDomain != null)
        {
            // Retrieve the multilang object name
            mlName = MultilingualTextProvider.
                getMultilangObjectName (this.getExtId (), this.getExtIdDomain (),
                    this.getName (), env);
        } // if
        // is this object a tab and do we have a tabcode
        else if (this.p_tabCode != null)
        {
            // try to get a multilingual name out of the TAB resource bundles
            String lookupKey = (new StringBuilder().
                append (MultilangConstants.LOOKUP_KEY_PREFIX_TAB).    
                append (this.getTabCode ())).toString ();
            
            // retrieve the multilingual name with the defined lookup key
            MultilingualTextInfo mlNameInfo = MultilingualTextProvider.getMultilingualTextInfo (
                    MultilangConstants.RESOURCE_BUNDLE_TABS_NAME,
                    MultilingualTextProvider.getNameLookupKey (lookupKey),
                    MultilingualTextProvider.getUserLocale (env),
                    env);
            
            // did we found a multilingual name for the tab?
            if (mlNameInfo.isFound ())
            {
                mlName = mlNameInfo.getMLValue ();
            } // if
        } // else if
        
        return mlName;
    } // getMlName


    /**************************************************************************
     * Get the parent node. <BR/>
     *
     * @return  The node.
     */
    public ObjectPathNode getParent ()
    {
        return this.p_parent;
    } // getParent


    /**************************************************************************
     * Get the number of child nodes. <BR/>
     *
     * @return  The number of childs.
     */
    public int getChildCount ()
    {
        return this.p_childs.size ();
    } // getChildCount


    /**************************************************************************
     * Get a specific child node. <BR/>
     *
     * @param   i       The number of the child within all child nodes
     *                  (the index).
     *
     * @return  The child node.
     */
    public ObjectPathNode getChild (int i)
    {
        if (i >= this.p_childs.size ())
        {
            return null;
        } // if

        return this.p_childs.elementAt (i);
    } // getChildCount


    /**************************************************************************
     * Check if the current node is a part of the parent node. <BR/>
     *
     * @return  <CODE>true</CODE> if the node is a part of the parent,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isPartOfParent ()
    {
        return this.p_partOfParent;
    } // isPartOfParent


    /*
     * SETTERS
     */
    /**************************************************************************
     * Set the type of the current node. <BR/>
     * The type must be one of {@link #TYPE_LEAF TYPE_LEAF},
     * {@link #TYPE_NODE TYPE_NODE}, or {@link #TYPE_ROOT TYPE_ROOT}.
     *
     * @param   nodeType    The type for the node.
     */
    public void setNodeType (int nodeType)
    {
        this.p_nodeType = nodeType;
    } // getnodeType


    /**************************************************************************
     * Set the name for the current node. <BR/>
     *
     * @param   name    The name for the node.
     */
    public void setName (String name)
    {
        this.p_name = name;
    } // setName
    
    
    /**************************************************************************
     * Set the ext key for the current node. <BR/>
     *
     * @param   extId       The extId for the node.
     * @param   extIdDomain The extIdDomain for the node.
     */
    public void setExtKey (String extId, String extIdDomain)
    {
        this.p_extId = extId;
        this.p_extIdDomain = extIdDomain;
    } // setExtKey

    
    /**************************************************************************
     * Set the tabCode. <BR/>
     *
     * @param   tabCode  The tabeCode node to be set.
     */
    public void setTabCode (String tabCode)
    {
        this.p_tabCode = tabCode;
    } // setTabCode


    /**************************************************************************
     * Set the parent node. <BR/>
     *
     * @param   parent  The parent node to be set.
     */
    public void setParent (ObjectPathNode parent)
    {
        this.p_parent = parent;
    } // setParent


    /**************************************************************************
     * Set the oid of the parent node. <BR/>
     *
     * @param   parentOid   The oid to be set.
     */
    public void setParentOid (OID parentOid)
    {
        this.p_parentOid = parentOid;
    } // setParentOid


    /**************************************************************************
     * Add a child to the current node. <BR/>
     *
     * @param   child   The child to be added.
     */
    public void addChild (ObjectPathNode child)
    {
        this.p_childs.addElement (child);
    } // addChild


    /**************************************************************************
     * Set if the current node is a part of the parent node. <BR/>
     *
     * @param   partOf  <CODE>true</CODE> if the node is a part of the parent
     *                  node; <CODE>false</CODE> otherwise.
     */
    protected void setPartOfParent (boolean partOf)
    {
        this.p_partOfParent = partOf;
    } // setPartOfParent


    /**************************************************************************
     * Retrieve the node data from db. <BR/>
     * The parent and the childs of current node will not be retrieved. <BR/>
     *
     * @return  Current node if there were some data found, or <CODE>null</CODE>
     *          if no data were found.
     *
     * @throws  DBError
     *          An exception occurred while getting the data from the database.
     */
    public ObjectPathNode performRetrieveNode () throws DBError
    {
        ObjectPathNode retVal = this;   // return value of method
        StringBuffer queryStr = new StringBuffer (); // the query string

        // compose the query:
        queryStr
            .append (" SELECT o.name, o.containerId, o.containerKind, t.code AS tabCode, ")
            .append ("        k.id AS extId, k.idDomain AS extDomain")
            .append (" FROM ibs_Object o ")
            .append ("    LEFT OUTER JOIN ibs_keymapper k ON o.oid = k.oid")
            .append ("    LEFT OUTER JOIN ibs_ConsistsOf c on o.consistsOfId = c.id")
            .append ("    LEFT OUTER JOIN ibs_Tab t on c.tabid = t.id")
            .append (" WHERE o.oid = " + this.p_oid.toStringQu ())
            .append (" AND o.state = " + States.ST_ACTIVE);

        try
        {
            // open db connection:
            SQLAction action = DBConnector.getDBConnection ();

            // perform the query:
            action.execute (queryStr, false);

            // get the tuple:
            if (!action.getEOF ())      // there was a tuple found?
            {
                // create a new object:
                this.p_name = action.getString ("name");
                this.setParentOid (SQLHelpers.getQuOidValue (action, "containerId"));
                this.p_partOfParent =
                    action.getInt ("containerKind") == BOConstants.CONT_PARTOF;
                this.p_extId = action.getString ("extId");
                this.p_extIdDomain = action.getString ("extDomain");
                this.p_tabCode = action.getString ("tabCode");
            } // if there was a tuple found
            else                        // no tuple found
            {
                // set corresponding return value:
                retVal = null;
            } // else no tuple found

            // end transaction:
            action.end ();

            // release db connection:
            DBConnector.releaseDBConnection (action);
        } // try
        catch (DBError e)
        {
            // should not occur, display error message:
            IOHelpers.printError ("Error when getting node data", e, false);
        } // catch DBError

        // return the computed return value:
        return retVal;
    } // performRetrieveNode


    /**************************************************************************
     * Retrieve the parent data from db, if there is no parent,
     * parent will be null.
     * The node data and the childs will not be retrieved. <BR/>
     *
     * @return  Parent node or
     *          <CODE>null</CODE> if there is no more parent node or
     *          the parent node is the workspace container.
     *
     * @throws  DBError
     *          There occurred a database error.
     */
    public ObjectPathNode performRetrieveParent () throws DBError
    {
        ObjectPathNode parent = null;
        boolean isPrivate = false;
        StringBuffer queryStr = new StringBuffer (); // the query string
        StringBuffer fromClause = new StringBuffer ();
        StringBuffer whereClause = new StringBuffer ();

        // create the outer joins:
        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_MenuTab_01"), new StringBuffer ("m"),
            new StringBuffer ("o.oid = m.objectOid"), new StringBuffer ("AND"),
            fromClause, whereClause);
        
        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_keymapper"), new StringBuffer ("k"),
            new StringBuffer ("o.oid = k.oid"), new StringBuffer ("AND"),
            fromClause, whereClause);

        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_ConsistsOf"), new StringBuffer ("c"),
            new StringBuffer ("o.consistsOfId = c.id"), new StringBuffer ("AND"),
            fromClause, whereClause);

        SQLHelpers.getLeftOuterJoin (
            new StringBuffer ("ibs_Tab"), new StringBuffer ("t"),
            new StringBuffer ("c.tabid = t.id"), new StringBuffer ("AND"),
            fromClause, whereClause);
        
        queryStr
            .append (" SELECT o.oid, o.name, o.containerId, o.containerKind, t.code AS tabCode")
            .append (", m.isPrivate, k.id AS extId, k.idDomain AS extDomain")
            .append (" FROM ibs_Object o").append (fromClause)
            .append (" WHERE o.oid = " + this.p_parentOid.toStringQu ())
            .append (" AND o.state = " + States.ST_ACTIVE)
            .append (whereClause);

        try
        {
            // open db connection:
            SQLAction action = DBConnector.getDBConnection ();

            // perform the query:
            action.execute (queryStr.toString (), false);

            // get the tuple:
            if (!action.getEOF ())      // there was a tuple found?
            {
                OID parentOid = SQLHelpers.getQuOidValue (action, "oid");

                // check if the parent is a workspace:
                isPrivate = action.getBoolean ("isPrivate");

                // check if the oid is valid and no workspace:
                if (!parentOid.isEmpty () && !isPrivate)
                // valid object?
                {
                    parent = new ObjectPathNode (parentOid);

                    parent.setName (action.getString ("name"));
                    parent.setExtKey (action.getString ("extId"), action.getString ("extDomain"));

                    parent.setTabCode (action.getString ("tabCode"));
                    
                    // get oid of the parent's parent:
                    parent.setParentOid (
                        SQLHelpers.getQuOidValue (action, "containerId"));

                    parent.setPartOfParent (
                        action.getInt ("containerKind") == BOConstants.CONT_PARTOF);
                } // if valid object
            } // if there was a tuple found

            // end transaction:
            action.end ();

            // release db connection
            DBConnector.releaseDBConnection (action);
        } // try
        catch (DBError e)
        {
            //...
        } // catch DBError

        // expand tree with parent node
        this.p_parent = parent;

        // check if there was a parent set:
        if (this.p_parent != null)      // parent was set?
        {
            this.p_parent.addChild (this);
        } // if parent was set

        return this.p_parent;
    } // performRetrieveParent


    /**************************************************************************
     * Get all Parents of this node. <BR/>
     *
     * @return  Enumeration of ObjectTreeNodes with this node as first
     *          element and root ObjectTreeNode as last element. <BR/>
     */
    public Enumeration<ObjectPathNode> getParents ()
    {
        ObjectPathNode parent = null;
        ObjectPathNode node = this;
        Vector<ObjectPathNode> parents = new Vector<ObjectPathNode> ();

        parents.addElement (node);

        while ((parent = node.getParent ()) != null)
        {
            node = parent;
            parents.addElement (node);
        } // while

        return parents.elements ();
    } // getParents


    /**************************************************************************
     * Get all root node of this node. <BR/>
     *
     * @return  root node of this node. <BR/>
     */
    public ObjectPathNode getRoot ()
    {
        ObjectPathNode parent = null;
        ObjectPathNode node = this;

        // if current node is root node
        if (node.getParent () == null)
        {
            return this;
        } // if

        while ((parent = node.getParent ()) != null)
        {
            node = parent;
        } // while

        return node;
    } // getParents


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        StringBuffer s = new StringBuffer ("--- PARENT TREE ---");
        s.append (this.parentTreeToString (this, s, 0));

        return s.toString ();
    } // toString


    /**************************************************************************
     * Convert the parent tree to string representation. <BR/>
     *
     * @param   node    The node, from which the parent shall be converted.
     * @param   s       The buffer, into which the result is written.
     * @param   level   The level of the current node.
     *
     * @return  The resulting string buffer. This is identical to s.
     */
    private StringBuffer parentTreeToString (ObjectPathNode node,
        StringBuffer s, int level)
    {
        if (node.p_parent != null)
        {
            s.append ("[" + level + "|")
                .append (node.p_parent.getOid ())
                .append ("|")
                .append ("" + node.p_parent.getName ())
                .append ("|")
                .append (node.p_parent.getNodeType ())
                .append ("|")
                .append (node.p_parent.isPartOfParent ())
                .append ("]");

            this.parentTreeToString (node.p_parent, s, level - 1);
        } // parent != null

        return s;
    } // String

} // class File_01

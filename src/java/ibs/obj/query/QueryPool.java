/*
 * Class: QueryPool.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.States;
import ibs.bo.type.TypeNotFoundException;
import ibs.io.Environment;
import ibs.obj.ml.Locale_01;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.list.ListException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The QueryPool contains all possible querytemplates which are marked
 * as systemqueries. <BR/>
 * Systemqueries are querytemplates where the 3'rd flag
 * of bit pattern queryType is TRUE. <BR/>
 *
 * @version     $Id: QueryPool.java,v 1.20 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ)
 ******************************************************************************
 */
public class QueryPool extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryPool.java,v 1.20 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     * Vector contains all queryCreatorObjects with queryType SYSTEM.
     */
    Vector<QueryCreator_01> queryCreators = new Vector<QueryCreator_01> ();

    /**
     * Vector contains all domainIds related to queryCreators.
     */
    Vector<Integer> queryCreatorDomainIds = new Vector<Integer> ();


    /**************************************************************************
     * Creates a QueryPool. <BR/>
     * initObject (...) and fill () has to be
     * called for initialization of QueryPool. <BR/>
     */
    public QueryPool ()
    {
        // nothing to do
    } // QueryPool


    /**************************************************************************
     * get count of querytemplates. <BR/>
     *
     * @return  Number of containing querytemplates.
     */
    public int size ()
    {
        return this.queryCreators.size ();
    } // size


    /**************************************************************************
     * retrieves all querytemplates from database and fills the pool. <BR/>
     *
     * @exception   DBError
     *              Something went wrong.
     */
    public void fill () throws DBError
    {
//trace ("AJ QueryPool.fill ()");
        SQLAction action = null;
        OID qcOid = null;
        Integer domainId = null;
        int domId = -1;
        int rowCount = 0;
        QueryCreator_01 queryCreator = null; // the actual query creator
        StringBuffer queryStr;          // the query string


        // get all querycreators of this domain with queryType SYSTEM
        // = 3'rd bit in bit pattern queryType

        // create querystring get all oids and related domainIds of all
        // querytemplates in db
        queryStr = new StringBuffer ()
            .append (" SELECT o.oid, dom.id")
            .append (" FROM ibs_Object o, ibs_QueryCreator_01 c,")
            .append ("      ibs_Domain_01 dom, ibs_Object odom")
            .append (" WHERE o.state = ").append (States.ST_ACTIVE)
            .append ("     AND o.oid = c.oid")
            .append ("     AND odom.oid = dom.oid")
            .append ("     AND SUBSTRING (o.posnopath, 1, 8) = odom.posnopath");
/* BB20060901: slow solution!
            .append ("     AND o.posnopath LIKE ")
            .append (SQLHelpers.getStrCat ("odom.posnopath", "'%'"));
*/
//trace ("AJ QueryPool.fill () QUERY=" + queryStr);

        // open db connection -  only workaround - db connection must
        // be handled somewhere else
        action = this.getDBConnection ();

        // execute query:
        rowCount = action.execute (queryStr, false);

        // empty resultset or error
        if (rowCount <= 0)
        {
            this.releaseDBConnection (action);
            return;         // leave method
        } // empty resultset or error

        // get tuples out of db:
        while (!action.getEOF ())
        {
            // store domainId for queryCreator:
            domId = action.getInt ("id");
            domainId = new Integer (domId);
            this.queryCreatorDomainIds.addElement (domainId);

            // get oid:
            qcOid = SQLHelpers.getQuOidValue (action, "oid");

            try
            {
                // get the object:
                queryCreator = (QueryCreator_01)
                    this.getObjectCache ().fetchObject
                        (qcOid, this.user, this.sess, this.env, false);

//trace ("QueryPool.fill (): added queryCreator [" + queryCreator.name + "] to querypool");
                // add the query creator to the list:
                this.queryCreators.addElement (queryCreator);
            } // try
            catch (ObjectNotFoundException e)
            {
                System.out.println ("Type object not found for oid " + qcOid +
                                    ": " + e.toString ());
//trace ("KR Object not found: " + obj + ".");
//showMessage ("Object not found: " + obj + ".");
                // show corresponding error message:
//                showMessage (BOMessages.MSG_OBJECTNOTFOUND);
            } // catch
            catch (TypeNotFoundException e)
            {
                System.out.println ("Type not found for oid " + qcOid +
                                    ": " + e.toString ());
//trace ("KR Object Type not found for oid " + oid + ".");
//showMessage ("KR Object Type not found for oid " + oid + ".");
                // show corresponding error message:
//                showMessage (e.getMessage ());
            } // catch
            catch (ObjectClassNotFoundException e)
            {
                System.out.println ("Object class not found for oid " + qcOid +
                                    ": " + e.toString ());
//trace ("KR Object class not found for type with oid " + typeOid + ".");
//showMessage ("KR Object class not found for oid " + oid + ".");
                // show corresponding error message:
//                showMessage (e.getMessage ());
            } // catch
            catch (ObjectInitializeException e)
            {
                System.out.println ("Object initialize exception for oid " +
                                    qcOid + ": " + e.toString ());
//trace ("KR Object could not be initialized for oid " + typeOid + ".");
                // show corresponding error message:
//                showMessage(e.getMessage ());
            } // catch
            catch (Exception e)
            {
//                showMessage ("KR Exception within fetchObject: " + e + "." + IE302.TAG_NEWLINE);
                System.out.println ("Exception for oid " + qcOid + ": ");
                ByteArrayOutputStream out = new ByteArrayOutputStream ();
                PrintStream stream = new PrintStream (out);
                e.printStackTrace (stream);
                System.out.println (out.toString ());
//                showMessage (out.toString ());
            } // catch

            action.next ();
        } // while

        // the last tuple has been processed
        // end transaction:
        action.end ();

        // close db connection in every case - only workaround -
        // db connection must be handled somewhere else:
        this.releaseDBConnection (action);
    } // fill


    /**************************************************************************
     * Get querytemplate with specific name in specific domain. <BR/>
     *
     * @param   name        name of required querytemplate
     * @param   domainId    id of current domain
     *
     * @return  Pointer to required query template.
     *
     * @exception   QueryNotFoundException
     *              is thrown if querytemplate with name
     *              &lt;name&gt; does not exist in domain with id &lt;domainId&gt;
     */
    public QueryCreator_01 fetch (String name, int domainId)
        throws QueryNotFoundException
    {
        // locale varibales:
        QueryCreator_01 qc = null;
        int qcDomainId = -1;

        // try to find required querycreator:
        for (int i = 0; name != null && i < this.queryCreators.size (); i++)
        {
            // get domainId of current querycreator:
            qcDomainId = (this.queryCreatorDomainIds.elementAt (i)).intValue ();

            // check if current querycreator is in the right domain:
            if (qcDomainId == domainId)
            {
                // get querycreator:
                qc = this.queryCreators.elementAt (i);

                // return query with required name:
                if (name.equals (qc.name))
                {
                    return this.queryCreators.elementAt (i);
                } // if
            } // if
        } // for

        // throw exception if query was not found:
        QueryNotFoundException qexc = new QueryNotFoundException (name);
        throw qexc;
    } // fetch


    /**************************************************************************
     * Get querytemplate with specific oid. <BR/>
     *
     * @param   aOid        objectId of required querytemplate
     *
     * @return  Pointer to required query template.
     *
     * @exception   QueryNotFoundException
     *              is thrown if querytemplate with objectId &lt;aOid&gt; does
     *              not exist.
     */
    public QueryCreator_01 fetch (OID aOid) throws QueryNotFoundException
    {
        for (int i = 0; aOid != null && i < this.queryCreators.size (); i++)
        {
            if (aOid.equals (
                (this.queryCreators.elementAt (i)).oid))
            {
                return this.queryCreators.elementAt (i);
            } // if
        } // for

        // throw exception if query was not found:
        QueryNotFoundException qexc = new QueryNotFoundException (aOid.toString ());
        throw qexc;
    } // fetch


    /**************************************************************************
     * update existing querycreator in pool or add new querycreator to pool. <BR/>
     * if querytype of existing query changes from system to an other type, the
     * querycreator is deleted from the pool.
     *
     * @param   qc      QueryCreator to be updated. (identified by OID)
     *
     * @return  <CODE>1</CODE>       if QueryCreator was found and updated.
     *          <CODE>2</CODE>       if QueryCreator was not found and added to pool.
     *          <CODE>-1</CODE>      if inputparameter qc is <CODE>null</CODE>
     */
    public int updateQuery (QueryCreator_01 qc)
    {
        // check inputparameters
        if (qc == null)
        {
            return -1;
        } // if

        // try to find querycreator with the same oid as the parameter
        for (int i = 0; i < this.queryCreators.size (); i++)
        {
            if (qc.oid.equals ((this.queryCreators.elementAt (i)).oid))
            {
                // set new query instead of old query
                this.queryCreators.setElementAt (qc, i);
                return 1;
            } // if
        } // for

        // Query does not exist in QueryPool
        // add query and domainid of query to pool
        this.queryCreators.addElement (qc);
        this.queryCreatorDomainIds.addElement (new Integer (qc.user.domain));

        return 2;
    } // updateQuery


    /**************************************************************************
     * delete query creator with specific oid from pool. <BR/>
     *
     * @param   queryOid    QueryCreator to be updated. (identified by OID).
     *
     * @return  <CODE>1</CODE>  if QueryCreator was found and deleted.
     *          <CODE>2</CODE>  if QueryCreator was not found in pool.
     *          <CODE>-1</CODE> if inputparameter qc is <CODE>null</CODE>
     */
    public int deleteQuery (OID queryOid)
    {
        // check inputparameters
        if (queryOid == null)
        {
            return -1;
        } // if

        // try to find querycreator with the same oid as the parameter
        for (int i = 0; i < this.queryCreators.size (); i++)
        {
            if (queryOid.equals ((this.queryCreators.elementAt (i)).oid))
            {
                // delete querycreator from pool
                this.queryCreators.removeElementAt (i);
                return 1;
            } // if
        } // for

        // Query does not exist in QueryPool
        return 2;
    } // deleteQuery


    /**************************************************************************
     * get QueryPool content as string. <BR/>
     *
     * @return  names and oids of all containing querycreators in a String
     */
    public String toString ()
    {
        String content = "";
        QueryCreator_01 qc = null;

        // try to find querycreator with the same oid as the parameter
        for (int i = 0; i < this.queryCreators.size (); i++)
        {
            qc = this.queryCreators.elementAt (i);
            content += "[" + qc.oid + ", " + qc.name + "]  ";
        } // for

        return content;
    } // toString

    
    /***************************************************************************
     * Load the multilang template info for all provided locales. <BR/>
     *
     * @param   locales  The locales to init the multilang info for
     * @param   env      The current environment
     * 
     * @throws  ListException, DBError
     */
    public final void loadMultilangInfo (Collection<Locale_01> locales, Environment env)
        throws ListException, DBError
    {
        // Check if queryCreators exists and if it is not empty 
        if (this.queryCreators == null || this.queryCreators.size () == 0)
        {
            // retrieve all querytemplates from database and fills the pool.
            this.fill ();
        } // if
        
        // load the multilingual values
        this.performLoadMultilangInfo (this.queryCreators, locales, env);
    } // loadMultilangInfo

    
    /***************************************************************************
     * Load the multilang template info for all provided locales for all
     * QueryCreator . <BR/>
     *
     * @aram    queryCreators   A vector with all Query Creators in the query pool 
     * @param   locales         The locales to init the multilang info for
     * @param   env             The current environment
     */
    public void performLoadMultilangInfo (Vector<QueryCreator_01> queryCreators, 
        Collection<Locale_01> locales, Environment env)
    {       
        // Iterate over all query creators and initialize the multilang info
        Iterator<QueryCreator_01> it = queryCreators.iterator ();
        while (it.hasNext ())
        {
            QueryCreator_01 qC = it.next ();
            qC.initMultilangInfo (locales, this.name, env);
        } // while        
    } // performLoadMultilangInfo
    
} // class QueryPool

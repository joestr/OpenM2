/*
 * Class: QueryExecutive.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.obj.query.QueryCreator_01;
import ibs.obj.query.QueryNotFoundException;
import ibs.obj.query.QueryPool;


/******************************************************************************
 * QueryFactory to generate clones of querytemplates in QueryPool. <BR/>
 * It's needed to work with this querytemplates, because it is not allowed to
 * change data in querytemplates of QueryPool. <BR/>
 *
 * @version     $Id: QueryFactory.java,v 1.9 2009/09/04 13:09:17 btatzmann Exp $
 *
 * @author      Andreas Jansa (AJ)
 ******************************************************************************
 */
public class QueryFactory extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryFactory.java,v 1.9 2009/09/04 13:09:17 btatzmann Exp $";


    /**************************************************************************
     * Creates a QueryFactory. <BR/>
     * initObject (...) has to be called for initialization of QueryFactory.
     * <BR/>
     */
    public QueryFactory ()
    {
        // nothing to do
    } // QueryFactory


    /**************************************************************************
     * get count of querytemplates in queryPool. <BR/>
     *
     * @return  count of querytemplates in queryPool
     */
    public int queryCount ()
    {
        if (this.app.queryPool == null)
        {
            return -1;
        } // if

        return ((QueryPool) this.app.queryPool).size ();
    } // setQueryPool


    /**************************************************************************
     * Creates a clone of querytemplate in QueryPool with specific name
     * in current domain. <BR/>
     *
     * @param   name    name of required querytemplate
     *
     * @return  Clone of required querytemplate in QueryPool.
     *
     * @exception   QueryNotFoundException
     *              is thrown if querytemplate with name
     *              &lt;name> does not exist in current domain.
     */
    public QueryCreator_01 get (String name) throws QueryNotFoundException
    {
        QueryCreator_01 qc = ((QueryPool) this.app.queryPool).fetch (name, this.env.getUserInfo ().getUser ().domain);
        QueryCreator_01 clone = (QueryCreator_01) qc.clone ();
        clone.user = this.user;
        clone.setEnv (this.env);
        clone.setSession (this.sess);
        clone.setTracerHolder (this.sess);

        return clone;
    } // get


    /**************************************************************************
     * Creates a clone of querytemplate in QueryPool with specific name
     * in current domain. <BR/>
     *
     * @param   aOid    objectId of required querytemplate
     *
     * @return  Clone of required querytemplate in QueryPool.
     *
     * @exception   QueryNotFoundException
     *              is thrown if querytemplate with oid
     *              &lt;aOid> does not exist in current domain
     */
    public QueryCreator_01 get (OID aOid) throws QueryNotFoundException
    {
        QueryCreator_01 qc = ((QueryPool) this.app.queryPool).fetch (aOid);
        QueryCreator_01 clone = (QueryCreator_01) qc.clone ();
        clone.user = this.user;
        clone.setEnv (this.env);
        clone.setSession (this.sess);
        clone.setTracerHolder (this.sess);

        return clone;
    } // get

} // class QueryFactory

/*
 * Class: QueryHelpers.java
 */

// package:
package ibs.obj.query;

//imports:
import ibs.app.AppMessages;
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.InputParamElement;
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;

import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/******************************************************************************
 * The helper class includes some useful methods that are used by other
 * query classes. <BR/>
 *
 * @version     $Id: QueryHelpers.java,v 1.6 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      klaus, 10.05.2005
 ******************************************************************************
 */
public abstract class QueryHelpers
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryHelpers.java,v 1.6 2010/04/15 15:31:13 rburgermann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * add Results of a m2 - systemquery to dom tree.
     *
     * the domtree-part for a query looks like this:
     *      ...........
     *      <VALUES>
     *          ......
     *            <VALUE FIELD="xxx" TYPE="QUERY" QUERYNAME="xxx">
     *                <INPARAMS>
     *                    <PARAMETER NAME="inpname1p">inpvalue1p</PARAMETER>
     *                    <PARAMETER NAME="inpname2p">inpvalue2p</PARAMETER>
     *                    ........
     *                    <PARAMETER NAME="inpname2p">inpvaluenp</PARAMETER>
     *                </INPARAMS>
     *                <RESULTROW>
     *                    <RESULTELEMENT NAME="xnamex" TYPE="xtypey">
     *                      xvaluex</RESULTELEMENT>
     *                </RESULTROW>
     *                <RESULTROW>
     *                    <RESULTELEMENT NAME="xnamex" TYPE="xtypey">
     *                      xvaluex</RESULTELEMENT>
     *                </RESULTROW>
     *            </VALUE>
     *          ......
     *      </VALUES>
     *
     *
     * @param   obj             Object, which is adding the query data.
     *                          This object has to contain the environment data
     *                          (app, sess, env, user, queryPool, etc.).
     * @param   queryValueNode  Node where the query result shall be added.
     * @param   currentObjOid   The oid of the current object.
     * @param   fieldName       Name of field, which contains the query.
     *                          This is used for getting query parameters from
     *                          environment.
     * @param   queryName       Name of the query to be executed.
     * @param   subTags         Sub tags of the node, possibly containing some
     *                          query parameters.
     * @param   env             The current environment                         
     */
    @SuppressWarnings("unchecked")
    public static final void addQueryData (BusinessObject obj,
                                           Node queryValueNode,
                                           OID currentObjOid,
                                           String fieldName,
                                           String queryName,
                                           Vector<?> subTags,
                                           Environment env)
    {
        Document doc = null;
        int inSize = 0;
        Vector<?> inParams = null;
        String oidString = null;
        QueryExecutive qe = null;
        // instantiation of all possible node objects
        Node rowNode = null;
        Element colNode = null;

        // get the DOM:
        doc = queryValueNode.getOwnerDocument ();
        // create the QueryExecutive object
        qe = new QueryExecutive ();
        // leider noch immer notwendig :(
        qe.initObject (obj.oid, obj.user, obj.getEnv (), obj.sess, obj.app);
        // set oid and containerId:
        qe.setCurrentObjectOid (obj.oid);
        qe.setCurrentContainerId (obj.containerId);

        if (currentObjOid != null)
        {
            oidString = currentObjOid.toString ();
        } // if
        else
        {
            oidString = OID.EMPTYOID;
        } // if

        // check if we got any subtags:
        if (subTags != null)
        {
            inParams = (Vector<?>) subTags.elementAt (0);
            inSize = inParams.size ();

            if (inSize > 0)                 // input parameters found?
            {
                for (int i = 0; i < inParams.size (); i++)
                                            // loop through all input parameters
                {
                    InputParamElement inParam = (InputParamElement) inParams.elementAt (i);

                    qe.addInParameter (inParam.getName (),
                        QueryConstants.FIELDTYPE_STRING,
                        BOHelpers.replaceSysVar (obj, inParam.getValue ()));
                } // for loop through all input parameters
            } // if input parameters found
        } // if

        // check if there where any parameters set in variable definition:
        if (inSize == 0)                // no fixed parameters set?
        {
            try
            {
                // get query creator:
                // important: this is the original query creator which
                // shall only be used for reading!
                QueryCreator_01 queryCreator =
                    ((QueryPool) obj.app.queryPool)
                        .fetch (queryName, obj.user.domain);

                // get environment values for the query search fields
                // if available:
                inParams = QueryExecutive_01.getSearchfieldParameters (
                    queryCreator.getInputParameters (),
                    obj.getEnv (), fieldName + "_");
                inSize = inParams.size ();

                if (inSize > 0)                 // input parameters found?
                {
                    // loop through all input parameters:
                    for (Iterator<QueryParameter> iter =
                            ((Vector<QueryParameter>) inParams).iterator ();
                        iter.hasNext ();)
                    {
                        QueryParameter qp = iter.next ();
                        if (qp.getName ().equals ("referenceOid"))
                        {
                            qp.setValue (oidString);
                        } // if
                        qe.addInParameter (qp);
                    } // for iter
                } // if input parameters found
            } // try
            catch (QueryNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace ();
            } // catch
        } // if no fixed parameters set

/* KR 20050418: the setting of the referenceOid is not longer necessary because
 *              this is done within setting the QueryCreator parameters above
        // check if there are any input parameters:
        if (inSize == 0)                // there are no input parameters
        {
            qe.addInParameter ("referenceOid",
                QueryConstants.FIELDTYPE_OBJECTID,
                oidString,
                QueryConstants.MATCH_EXACT);
        } // if there are no input parameters
 */

        // if query with name exist and could be executed
        if (qe.execute (queryName))
        {
            int columnCount = qe.getColCount ();

            while (!qe.getEOF ())
            {
                // create row node
                rowNode = doc.createElement (DIConstants.ELEM_RESULTROW);

                for (int i = 0; i < columnCount; i++)
                {
                    // create nodes for resultelements
                    colNode = doc.createElement (DIConstants.ELEM_RESULTELEMENT);
                    colNode.setAttribute (DIConstants.ATTR_NAME, qe
                        .getColName (i));
                    colNode.setAttribute (DIConstants.ATTR_TYPE, qe
                        .getColType (i));
                    colNode.setAttribute (DIConstants.ATTR_MULTIPLE, qe
                        .getMultipleAttribute (i) ? DIConstants.ATTRVAL_YES :
                        DIConstants.ATTRVAL_NO);
                    // add multilang information
                    colNode.setAttribute (DIConstants.ATTR_MLNAME, qe
                        .getMlColName (i));
                    colNode.setAttribute (DIConstants.ATTR_MLDESCRIPTION, qe
                        .getMlColDescription (i));

                    // check if type is BOOLEAN
                    // in that case we need to use tokens as values
                    if (qe.getColType (i).equals (QueryConstants.COLUMNTYPE_BOOLEAN))
                    {
                        // replace the true/false values with the appropriate tokens
                        // note that this can also be done in the stylesheet
                        if (qe.getColValue (i).equalsIgnoreCase ("true"))
                        {
                            colNode.appendChild (doc.createTextNode ( 
                                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                    AppMessages.ML_MSG_BOOLTRUE, env)));
                        } // if (qe.getColValue (i).equalsIgnoreCase ("true"))
                        else    // false value
                        {
                            colNode.appendChild (doc.createTextNode ( 
                                MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                    AppMessages.ML_MSG_BOOLFALSE, env)));
                        } // else  false value
                    } // if (qe.getColType (i).equals (QueryConstants.COLUMNTYPE_BOOLEAN))
                    else    // not a boolean type
                    {
                        colNode.appendChild (
                            doc.createTextNode (qe.getColValue (i)));
                    } // not a boolean type

                    // add columnnode to rownode in domtree
                    rowNode.appendChild (colNode);
                } // for

                // add rownode to queryNode
                queryValueNode.appendChild (rowNode);  // WEG
                qe.next ();
            } // while
        } // if
    } // addQueryData



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor is just to ensure that there is no default constructor
     * generated during compilation. <BR/>
     */
    private QueryHelpers ()
    {
        // nothing to do
    } // QueryHelpers

} // class QueryHelpers

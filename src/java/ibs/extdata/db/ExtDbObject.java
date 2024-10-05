/*
 * Class: ExtDbObject.java
 */

// package:
package ibs.extdata.db;

// imports:
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.XMLViewer_01;
import ibs.io.LayoutConstants;
import ibs.obj.query.QueryConstants;
import ibs.obj.query.QueryExecutive;
import ibs.service.user.User;
import ibs.tech.html.Page;
import ibs.tech.html.StyleSheetElement;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/******************************************************************************
 * Super Class for Objects which gets its Data and Content via DB Query Objects.
 * <BR/>
 *
 * @version     $Id: ExtDbObject.java,v 1.10 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author  Andreas Jansa (AJ)
 ******************************************************************************
 */
public class ExtDbObject extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExtDbObject.java,v 1.10 2010/04/15 15:31:13 rburgermann Exp $";


    /**************************************************************************
     * Creates a ExtDbObject. <BR/>
     * initObject (...) has to be called for initialization of ExtDbObject.
     * <BR/>
     */
    public ExtDbObject ()
    {
        super ();
    } // ExtDbObject


    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewer_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ExtDbObject (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // XMLViewer_01


    /**************************************************************************
     * Insert style sheet information in a standard info view. <BR/>
     *
     * @param   page    The page into which the style sheets shall be inserted.
     */
    protected void insertInfoStyles (Page page)
    {
        super.insertInfoStyles (page);
        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS =
            this.sess.activeLayout.path + this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);
    } // insertInfoStyles


    /**************************************************************************
     * add Results of a m2 - systemquery to dom tree.
     * use Property p_virtualId as Inputparameter ID for Queries and
     * current ID of Catalog from session as Inputparameter CatID.
     *
     * @param   queryValueNode  Values for the query.
     * @param   viewMode        Query view mode
     * @param   queryName       Name of the query
     * @param   subTags         The subtags.
     *
     * the domtree-part for a query looks like this:
     * <pre>
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
     * </pre>
     */
    protected void addQueryData (Node queryValueNode, int viewMode,
        String queryName, Vector<?> subTags)
    {
        Document doc = queryValueNode.getOwnerDocument ();
        QueryExecutive qe = new QueryExecutive ();
        // leider noch immer notwendig :(
        qe.initObject (this.oid, this.user, this.env, this.sess, this.app);

        this.setQueryInputParameter (qe);

        // instantiation of all possible node objects
        Node rowNode = null;
        Element colNode = null;

        // if query with name exist and could be executed
        if (qe.execute (queryName))
        {
            int columnCount = qe.getColCount ();

            while (!qe.getEOF ())
            {
                // create row node
                rowNode = doc.createElement ("RESULTROW");

                for (int i = 0; i < columnCount; i++)
                {
                    // instantiate nodes for result elements
                    colNode = doc.createElement ("RESULTELEMENT");
                    colNode.setAttribute (DIConstants.ATTR_NAME, qe
                        .getColName (i));
                    colNode.setAttribute (DIConstants.ATTR_TYPE, qe
                        .getColType (i));
                    colNode.appendChild (doc
                        .createTextNode (qe.getColValue (i)));
                    // add multilang information
                    colNode.setAttribute (DIConstants.ATTR_MLNAME, qe
                        .getMlColName (i));
                    colNode.setAttribute (DIConstants.ATTR_MLDESCRIPTION, qe
                        .getMlColDescription (i));
                    
                    // add column node to row node in dom tree
                    rowNode.appendChild (colNode);
                } // for

                // add row node to queryNode
                queryValueNode.appendChild (rowNode);  // WEG
                qe.next ();
            } // while
        } // if
    } // addQueryData


    /**************************************************************************
     * Set input parameters for a query executive. <BR/>
     *
     * @param   qe      The query executive.
     */
    protected void setQueryInputParameter (QueryExecutive qe)
    {
        if (this.p_virtualId != null)
        {
            qe.addInParameter ("ID",
                QueryConstants.FIELDTYPE_STRING,
                this.p_virtualId,
                QueryConstants.MATCH_EXACT);
        } // if
    } // setQueryInputParameter


    /**************************************************************************
     * Gets values of the buttons and returns the values as an aray of buttons. <BR/>
     * Do not show any buttons.
     *
     * @param   representationForm  Kind of representation.
     * @param   isContentView       Display the Info-View buttons or the Content-view buttons
     *
     * @return  An array which includes buttons
     */
    protected int[] buildButtonBar (int representationForm, boolean isContentView)
    {
        return new int []
        {
            Buttons.BTN_BACK,
        }; // buttons
    } // buildButtonBar

} // class ExtDbObject

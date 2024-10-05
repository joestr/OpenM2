/*
 * Class: QueryLinkTabView.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.obj.query.QueryExecutive_01;
import ibs.service.user.User;


/******************************************************************************
 * This class could be used as TabView for an other object. <BR/>
 * The content of the tab is shown via a query.
 * The Buttons 'new and reference' and 'search and reference' are shown
 * to create links to objects via search. <BR/>
 *
 * It should be used for a TABOBJECT definition in a XML-Type. <BR/>
 *
 * syntax in XML:
 * <TABOBJECT TABCODE="[code]" KIND="VIEW" CLASS="ibs.obj.query.QueryLinkTabView">
 *   <SYSTEM>
 *     <NAME>[name]</NAME>
 *     <DESCRIPTION>[description]</DESCRIPTION>
 *   </SYSTEM>
 *   <VALUES>
 *     <VALUE FIELD="viewQuery" TYPE="TEXT">[queryname]</VALUE>
 *     <VALUE FIELD="searchQuery" TYPE="TEXT">[queryname]</VALUE>
 *     <VALUE FIELD="newQuery" TYPE="TEXT">[queryname]</VALUE>
 *   </VALUES>
 * </TABOBJECT>
 *
 * @version     $Id: QueryLinkTabView.java,v 1.5 2009/07/24 10:21:08 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 011115
 ******************************************************************************
 */
public class QueryLinkTabView extends QueryExecutive_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryLinkTabView.java,v 1.5 2009/07/24 10:21:08 kreimueller Exp $";


    /**
     * query for search and link. <BR/>
     */
    public String searchQuery = null;


    /**
     * query for new and link. <BR/>
     */
    public String newQuery = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class QueryLinkTabView.
     * <BR/>
     */
    public QueryLinkTabView ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // QueryLinkTabView


    /**************************************************************************
     * Initializes a QueryExecutive object. <BR/>
     * The compound object id is stored in the <A HREF="#oid">oid</A> property
     * of this object. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * <A HREF="#env">env</A> is initialized to the provided object. <BR/>
     * <A HREF="#sess">sess</A> is initialized to the provided object. <BR/>
     * <A HREF="#app">app</A> is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);
    } // initObject


    /**************************************************************************
     * This constructor creates a new instance of the class QueryLinkTabView.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QueryLinkTabView (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // QueryLinkTabView


    /***************************************************************************
     * Set the specific properties for this specific tabview. <BR/>
     *
     * @param   majorObject The major object of this view tab.
     */
    public void setSpecificProperties (BusinessObject majorObject)
    {
//this.debug ("QueryTabView.setSpecificProperties (BusinessObject majorObject)");
        DataElement tabData =
            ((XMLViewer_01) majorObject).getTabData (this.p_tabId);

        // set system values
        this.name = tabData.name;
        this.description = tabData.description;

        // get the view specific values
        // viewQuery
        if (tabData.exists ("viewQuery"))
        {
            this.queryObjectName = tabData.getImportStringValue ("viewQuery");
        } // if

        // searchQuery
        if (tabData.exists ("searchQuery"))
        {
            this.searchQuery = tabData.getImportStringValue ("searchQuery");
            this.sess.searchQuery = this.searchQuery;
        } // if

        // newQuery
        if (tabData.exists ("newQuery"))
        {
            this.newQuery = tabData.getImportStringValue ("newQuery");
            this.sess.newQuery = this.newQuery;
        } // if

        // currentObjectOid
        this.currentObjectOid = majorObject.oid;

        // prepare query creator with current parameters
        this.prepareQueryCreator ();
    } // setSpecificProperties


   /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     *
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_SEARCHANDREFERENCE,
            Buttons.BTN_NEWANDREFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class QueryLinkTabView

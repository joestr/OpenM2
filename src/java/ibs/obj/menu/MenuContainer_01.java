/*
 * Class: MenuContainer_01.java
 */

// package:
package ibs.obj.menu;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;

import java.util.Vector;


/******************************************************************************
 * This class represents the Container for all tabs of one user. <BR/>
 *
 * @version     $Id: MenuContainer_01.java,v 1.15 2010/04/28 10:02:56 btatzmann Exp $
 *
 * @author      Daniel Janesch (DJ), 000621
 ******************************************************************************
 */
public class MenuContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MenuContainer_01.java,v 1.15 2010/04/28 10:02:56 btatzmann Exp $";


    /**
     * The User ID of current user
     */
    public int menuOwner;

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class MenuContainer_01.
     * <BR/>
     */
    public MenuContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // MenuContainer_01


    /**************************************************************************
     * Creates a MenuContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @see     ibs.bo.BusinessObject
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public MenuContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // MenuContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set common attributes:
        this.viewContent = "v_MenuContainer_01$content";

        // set the instance's attributes:
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // functions called from application level
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes uid and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT priorityKey, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE 0 = 0 ";<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        // sets the container for the tabs null if the current user has allready
        // one container
        if (this.user.id == this.menuOwner)
        {
            this.sess.menus = null;
        } // if

///////////////////////////////
//
// BEGIN Performancetuning: removed usage of view v_MenuContainer_01$content
//
        return new StringBuilder ()
            .append ("SELECT menu.oid, menu.name, menu.priorityKey,")
                .append (" menu.oLevel, menu.posNoPath,")
                .append (" menu.classFront, menu.classBack, menu.fileName,")
                .append (" menu.levelStep, menu.levelStepMax,")
                .append (" menu.userId, menu.rights, k.id AS extId,")
                .append (" k.idDomain AS extDomain")
            .append (" FROM ((")
            // first select gets all public containers:
                .append ("SELECT m1.priorityKey, m1.objectoid as oid,")
                    .append (" r.oLevel, r.posNoPath,")
                    .append (" m1.domainId, m1.classFront, m1.classBack,")
                    .append (" m1.filename, m1.levelStep, m1.levelStepMax,")
                    .append (" r.userId AS userId, r.rights AS rights, r.name")
                .append (" FROM ibs_MenuTab_01 m1, v_Container$rights r")
                .append (" WHERE m1.isPrivate = 0")
                    .append (" AND m1.objectoid = r.oid")
                    .append (" AND r.userid = ").append (this.getUser ().id)
                    .append (" AND r.state = ").append (States.ST_ACTIVE)
                .append (" )")
                .append (" UNION ALL")
                .append (" (")
            // second select gets workspace of current user (private-container):
                .append (" SELECT m2.priorityKey, w.workspace AS oid,")
                    .append (" o.oLevel, o.posNoPath,")
                    .append (" m2.domainId, m2.classFront, m2.classBack,")
                    .append (" m2.fileName, m2.levelStep, m2.levelStepMax,")
                    .append (" w.userId AS userId,")
                    .append (" 2147483647 AS rights, o.name")
                .append (" FROM ibs_MenuTab_01 m2, ibs_Workspace w,")
                    .append (" ibs_Object o")
                .append (" WHERE m2.isPrivate = 1")
                    .append (" AND m2.objectoid = o.containerId")
                    .append (" AND w.workspace = o.oid ")
                    .append (" AND w.userid = ").append (this.getUser ().id)
                    .append (" AND o.state = ").append (States.ST_ACTIVE)
                .append (" )) menu  left outer join ibs_keymapper k on menu.oid = k.oid")
            .append (" WHERE  0 = 0")
            .toString ();
//
// END Performancetuning
//
///////////////////////////////
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue ("&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within
     * this method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // instantiate a Dataclass for storing information of the tabs
        MenuData_01 menu = new MenuData_01 ();
        // get common attributes:

        // get element type specific attributes from the database:
        if (this.user.id == this.menuOwner) // tab exists already for the user?
        {
            menu.priorityKey = action.getInt ("priorityKey");
            menu.name = action.getString ("name");
            menu.oid = SQLHelpers.getQuOidValue (action, "oid");
            menu.p_oLevel = action.getInt ("oLevel");
            menu.p_posNoPath = action.getString ("posNoPath");
            menu.classFront = action.getString ("classFront");
            menu.classBack = action.getString ("classBack");
            menu.filename = action.getString ("filename");
            menu.p_levelStep = action.getInt ("levelStep");
            menu.p_levelStepMax = action.getInt ("levelStepMax");
            menu.p_extId = action.getString ("extId");
            menu.p_extIdDomain = action.getString ("extDomain");

            if (this.sess.menus == null)// vector allready exists?
            {
                this.sess.menus = new Vector<MenuData_01> ();
            } // if vector allready exists

            // adds the information of one tab to the Container which includes
            // all tabs for one User
            this.sess.menus.addElement (menu);
        } // if tab exists already for the user
    } // getContainerElementData


    ///////////////////////////////////////////////////////////////////////////
    // viewing functions
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
//            Buttons.BTN_EDIT,
//            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
            Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
//            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
            Buttons.BTN_HELP,
//            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        this.headings = new String [] {""};
        this.orderings = new String [] {"priorityKey"};

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class MenuContainer_01

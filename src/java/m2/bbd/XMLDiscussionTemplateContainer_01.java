/*
 * Class: XMLDiscussionTemplateContainer_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.bo.Buttons;
import ibs.bo.OID;
import ibs.di.DocumentTemplateContainer_01;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type XMLDiscussionTemplateContainer with
 * version 01. <BR/>
 *
 * @version     $Id: XMLDiscussionTemplateContainer_01.java,v 1.6 2009/07/25 00:40:44 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 000925
 ******************************************************************************
 */
public class XMLDiscussionTemplateContainer_01 extends DocumentTemplateContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLDiscussionTemplateContainer_01.java,v 1.6 2009/07/25 00:40:44 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * XMLDiscussionTemplateContainer_01. <BR/>
     */
    public XMLDiscussionTemplateContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussionTemplateContainer_01


    /**************************************************************************
     * Creates a XMLDiscussionTemplateContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public XMLDiscussionTemplateContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussionTemplateContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.viewContent = "v_XMLDiscTempContainer_01$cont";
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append ("SELECT * ")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE  1 = 1")
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * This method adds a contstraint to the existing query that excludes all
     * XMLDiscussionTemplates out of the resultset which are referenced by other
     * objects (XMLDiscussions). <BR/>
     * Overides the method of container. It must be start with "AND...".
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  The extension to the query.
     *
     * @see ibs.bo.Container#createQueryRetrieveContentData
     */
    protected StringBuffer extendQueryRetrieveDeleteData ()
    {
//trace ("extendQueryRetrieveDeleteData");
        return
            new StringBuffer ()
                .append (" AND oid NOT IN")
                .append (" (SELECT x.oid")
                    .append (" FROM m2_XmlDiscussionTemplate_01 x, m2_Discussion_01 d,")
                        .append (" ibs_Object o")
                    .append (" WHERE d.oid = o.oid")
                    .append (" AND o.state = 2")
                    .append (" AND x.oid = d.refOid")
                .append (")");
/*
            " AND oid NOT IN" +
                " (SELECT x.oid" +
                " FROM m2_XmlDiscussionTemplate_01 x," +
                    " (SELECT d.refOid" +
                    " FROM m2_Discussion_01 d, ibs_Object o" +
                    " WHERE d.oid = o.oid" +
                    " AND o.state = 2) v" +
                " WHERE x.oid = v.refOid" +
                ")";
*/
    } // extendQueryRetrieveDeleteData


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
            Buttons.BTN_NEW,
            Buttons.BTN_PASTE,
            Buttons.BTN_REFERENCE,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_CUT,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons sepp

} // XMLDiscussionTemplateContainer_01

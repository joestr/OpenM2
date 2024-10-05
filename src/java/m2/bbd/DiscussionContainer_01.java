/*
 * Class: DiscussionContainer_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BuildException;
import ibs.tech.html.GroupElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import m2.bbd.BbdConstants;
import m2.bbd.BbdTokens;
import m2.bbd.DiscussionContainerElement_01;


/******************************************************************************
 * This class represents one object of type DiscussionContainer with
 * version 01. <BR/>
 *
 * @version     $Id: DiscussionContainer_01.java,v 1.26 2010/05/19 15:19:03 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 980504
 ******************************************************************************
 */
public class DiscussionContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiscussionContainer_01.java,v 1.26 2010/05/19 15:19:03 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * DiscussionContainer_01. <BR/>
     */
    public DiscussionContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // DiscussionContainer_01


    /**************************************************************************
     * Creates a DiscussionContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DiscussionContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // DiscussionContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // a own create procedure because of the tab
        this.procCreate = "p_DiscContainer_01$create";

        this.elementClassName = "m2.bbd.DiscussionContainerElement_01";
        this.viewContent = "v_DiscussionContainer_01$cont";
    } // initClassSpecifics


    /**************************************************************************
     * Represent the content of the Container, i.e. its elements, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is
     *                              sorted.
     * @param   orderHow            Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    public void performShowContent (int representationForm,
                                    int orderBy, String orderHow)
    {
        DiscussionContainerElement_01 actElem;       // actual object
        int i;                          // actual index
        int size = this.elements.size (); // number of elements within this
                                        // container

        // start with the container representation: show header
        Page page = new Page ("List", false); // the output page

        // Stylesheetfile wird geladen
        StyleSheetElement style = new StyleSheetElement ();
        style.importSS = this.sess.activeLayout.path +
            this.env.getBrowser () +
            "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETLIST].styleSheet;
        page.head.addElement (style);


        // set the icon of this object:
        this.setIcon ();

        GroupElement body;
        if (this.isTab ())
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env), 
                this.getNavItems (), this.containerName, this.icon, size);
        } // if
        else
        {
            body = this.createHeader (page, 
                MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    this.headingName, new String[] {this.name}, this.env),
                this.getNavItems (), null, this.icon, size);
        } // else

        TextElement text = new TextElement ("text");

        // show description of container:
        body.addElement (this.createDescription ());

        if (size > 0)               // there are some elements?
        {
            int columns = this.headings.length + 1;
            TableElement table;
            RowElement tr;

/* ******* */
/* HACK!!! */
/* ******* */
            // set alignments:
            String[] alignments = new String[columns - 1];
            alignments[0] = "" + IOConstants.ALIGN_RIGHT + "\">" +
                "<COL CLASS=\"" + CssConstants.CLASS_NAME + "\"" +
                " ALIGN=\"" + IOConstants.ALIGN_LEFT;
            // set alignments for all columns:
            for (i = 1; i < columns - 1; i++)
            {
                alignments[i] = null;
            } // for i
/* ************ */
/* HACK ENDE!!! */
/* ************ */

            // create table definition for list:
            table = new TableElement (columns);
            table.classId = CssConstants.CLASS_LIST;
            table.frametypes = IOConstants.FRAME_VOID;
            table.width = HtmlConstants.TAV_FULLWIDTH;
            table.alignment = alignments;

            // create header
            tr = this.createHeading (this.headings,
                                this.orderings, orderBy, orderHow);

            // add header to table:
            table.addElement (tr, true);


            // loop through all elements of this container and display them.
            for (i = 0; i < size; i++)
            {
                actElem = (DiscussionContainerElement_01) this.elements.elementAt (i);
                actElem.showExtendedAttributes = this.getUserInfo ().userProfile.showExtendedAttributes;

                table.addElement (actElem.show
                    (BOListConstants.LST_CLASSROWS[i % BOListConstants.LST_CLASSROWS.length], env));
            } // for i

            if (this.getUserInfo ().userProfile.showRef)
            {
                this.showRefs (table, page);
            } // if

            // finish the container representation: show footer
            body.addElement (table);

        } // if there are some elements
        else // there are no elements
        {
            // show the according message to the user:
            TableElement table = new TableElement ();
            table.width = HtmlConstants.TAV_FULLWIDTH;
            RowElement tr = new RowElement (2);
            text = new TextElement (MultilingualTextProvider.getMessage(this.msgBundleContainerEmpty,
                    this.msgContainerEmpty, new String[] {this.name}, env));
            TableDataElement td = new TableDataElement (text);
            td.classId = CssConstants.CLASS_BODY;
            tr.addElement (td);
            table.addElement (tr);

            if (this.getUserInfo ().userProfile.showRef)
            {
                this.showRefs (table, page);
                body.addElement (table);
            } // if
        } // else there are no elements

        if (this.p_isShowCommonScript)
        {
            // create the script to be executed on client:
            ScriptElement script = this.getCommonScript (true);
            page.body.addElement (script);
        } // if

        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowContent


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append ("SELECT  DISTINCT oid, state, name, typeName, isLink, linkedObjectId,")
            .append ("        owner, ownerName, ownerOid, ownerFullname, lastChanged,")
            .append ("        isNew, icon, unknownMessages, description")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE  containerId = ").append (this.oid.toStringQu ())
            .append (" ")
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue ("&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        super.getContainerElementData (action, commonObj);
        DiscussionContainerElement_01 obj = (DiscussionContainerElement_01) commonObj;
        obj.newEntries = action.getInt ("unknownMessages");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        int length = 0;
        int counter = 0;

        // check if columns shall be reduced
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduced list
            // set headings:
            // get the length of the array
            length = BOListConstants.LST_HEADINGS_REDUCED.length;

            // create an array which has place for one more entry
            this.headings = new String[length + 1];

            // fill the array with the standard elements
            for (counter = 0; counter < length; counter++)
            {
                this.headings[counter] = 
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOListConstants.LST_HEADINGS_REDUCED[counter], env);
            } // for

            // add the special element for discussion to the array
            this.headings[counter] = 
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE, 
                BbdTokens.ML_NEWENTRIES, env);

            // set ordering attributes for the corresponding headings:
            // get the length of the array
            length = BOListConstants.LST_ORDERINGS_REDUCED.length;

            // create an array which has place for one more entry
            this.orderings = new String[length + 1];

            // fill the array with the standard elements
            for (counter = 0; counter < length; counter++)
            {
                this.orderings[counter] =
                    BOListConstants.LST_ORDERINGS_REDUCED[counter];
            } // for

            // add the special element for discussion to the array
            this.orderings[counter] = BbdConstants.ORD_NEWENTRIES;
        } // if
        else
        {
            // extended list
            // set headings:
            // get the length of the array
            length = BOListConstants.LST_HEADINGS.length;

            // create an array which has place for one more entry
            this.headings = new String[length + 1];

            // fill the array with the standard elements
            for (counter = 0; counter < length; counter++)
            {
                this.headings[counter] = 
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                    BOListConstants.LST_HEADINGS[counter], env);
            } // for

            // add the special element for discussion to the array
            this.headings[counter] = 
                MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE, 
                BbdTokens.ML_NEWENTRIES, env);

            // set ordering attributes for the corresponding headings:
            // get the length of the array
            length = BOListConstants.LST_ORDERINGS.length;

            // create an array which has place for one more entry
            this.orderings = new String[length + 1];

            // fill the array with the standard elements
            for (counter = 0; counter < length; counter++)
            {
                this.orderings[counter] = BOListConstants.LST_ORDERINGS[counter];
            } // for

            // add the special element for discussion to the array
            this.orderings[counter] = BbdConstants.ORD_NEWENTRIES;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class DiscussionContainer_01

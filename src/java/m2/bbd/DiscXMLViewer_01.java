 /*
 * Class: DiscXMLViewer_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.NoMoreElementsException;
import ibs.bo.OID;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.di.DocumentTemplate_01;
import ibs.di.XMLViewer_01;
import ibs.io.HtmlConstants;
import ibs.io.IOHelpers;
import ibs.io.LayoutConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.BuildException;
import ibs.tech.html.GroupElement;
import ibs.tech.html.Page;
import ibs.tech.html.RowElement;
import ibs.tech.html.ScriptElement;
import ibs.tech.html.StyleSheetElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.util.NoAccessException;


/******************************************************************************
 * The DiscXMLViewer Object presents itself as an businessobject with an XML
 * file attached that stores the data.
 *
 * @version     $Id: DiscXMLViewer_01.java,v 1.24 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Keim Christine (CK), 001009
 ******************************************************************************
 */
public class DiscXMLViewer_01 extends XMLViewer_01 implements DiscussionEntry
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiscXMLViewer_01.java,v 1.24 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * level of the discXMLViewer
     */
    public int level = -1;

    /**
     * the oid of the discussion where this object is in. <BR/>
     */
    public OID discussionId = null;

    /**
     * has the Entry Subentries. <BR/>
     */
    public int hasSubEntries = 0;

    /**
     * Rights of the user on the Entry. <BR/>
     */
    public int rights;


    /**************************************************************************
     * This constructor creates a new instance of the class DiscXMLViewer_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public DiscXMLViewer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // init specifics of actual class:
    } // DiscXMLViewer_01


    /**************************************************************************
     * This constructor creates a new instance of the class XMLViewer_01. <BR/>
     */
    public DiscXMLViewer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // init specifics of actual class:
    } // DiscXMLViewer_01


    /**************************************************************************
     * Get the oid of the object which is the virtual container of the actual
     * object. <BR/>
     * For standard object this is the containerId.
     *
     * @return  The oid of the object which is the container of the actual
     *          object.
     *          <CODE>null</CODE> if there is no container found.
     */
    public OID getMajorContainerOid ()
    {
        // just return the actual container oid:
        return this.discussionId;
    } // getMajorContainerOid


    /**************************************************************************
     * Get the oid of the object which is in the hierarchy above the actual
     * object out of the database. <BR/>
     *
     * @return  The oid of the object which is above the actual object.
     *          null if there is no upper object.
     *
     * @exception   NoMoreElementsException
     *              The object is the topmost one.
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotFoundException
     *              The object was not found.
     */
    public OID getUpperOid ()
        throws NoAccessException, ObjectNotFoundException, NoMoreElementsException
    {
        OID upperOid = null;            // oid of upper object
        if (true)                       // actual object resists on this
                                        // server?
        {
            // try to retrieve the upper object of this object:
            upperOid = this.discussionId;
        } // if actual object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
        return upperOid;                // return the oid of the upper object
    } // getUpperOid


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        // set extended search flag
        this.searchExtended = false;
        // set db table name
        this.tableName = "m2_Beitrag_01";

        this.procCreate = "p_DiscXMLViewer_01$create";
        this.procChange = "p_XMLViewer_01$change";
        this.procRetrieve = "p_DiscXMLViewer_01$retrieve";
        this.procDelete = "p_DiscXMLViewer_01$delete";

        // set number of parameters for procedure calls:
    // haeh?
        //this.specificCreateParameters = 2;
    // haeh?
        this.specificChangeParameters = 2;
        this.specificRetrieveParameters = 5 + 4;
    } // initClassSpecifics


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = super.setSpecificRetrieveParameters (sp, params, lastIndex);

        // set the specific parameters:
        // level
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // hasSubEntries
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // rights
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);
        // discussionId
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data
     *                      stored procedure.
     * @param   lastIndex   The index to the last element used in params thus
     *                      far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
//        super.getSpecificRetrieveParameters (params, lastIndex);
        int i = lastIndex;

        // momentary hardcoded - bug in the basis
        this.p_templateObj = (DocumentTemplate_01)
            this.getTypeCache ().getTemplate (SQLHelpers.getSpOidParam (params[++i]));
        this.workflowTemplateOid = SQLHelpers.getSpOidParam (params[++i]);
        this.systemDisplayMode = params[++i].getValueInteger ();
        // ignore the isDBMapped parameter which is not longer relevant:
        i++;
        this.isShowDOMTree = params[++i].getValueBoolean ();

        // get the specific parameters:
        this.level = params[++i].getValueInteger ();
        this.hasSubEntries = params[++i].getValueInteger ();
        this.rights = params[++i].getValueInteger ();
        this.discussionId = SQLHelpers.getSpOidParam (params[++i]);
    } // getSpecificRetrieveParameters


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
        int [] buttons;

        if (this.level >= 3) // no answer is possible anymore
        {
            int [] temp;
            if (this.hasSubEntries > 0) // it is not possible anymore to delete
                                        // and to edit
            {
                // define buttons to be displayed:
                int [] temp2 =
                {
                    Buttons.BTN_DISTRIBUTE,
                }; // buttons
                temp = temp2;
            } // if
            else
            {
                // define buttons to be displayed:
                int [] temp2 =
                {
                    Buttons.BTN_EDIT,
                    Buttons.BTN_DELETE,
                    Buttons.BTN_DISTRIBUTE,
                }; // buttons
                temp = temp2;
            } // else
            buttons = temp;
        } // if
        else  // answer is possible
        {
            int [] temp;
            if (this.hasSubEntries > 0) // it is not possible anymore to delete
                                        // and to edit?
            {
                // define buttons to be displayed:
                int [] temp2 =
                {
                    Buttons.BTN_ANSWER,
                    Buttons.BTN_DISTRIBUTE,
                }; // buttons
                temp = temp2;
            } // if
            else
            {
                // define buttons to be displayed:
                int [] temp2 =
                {
                    Buttons.BTN_ANSWER,
                    Buttons.BTN_EDIT,
                    Buttons.BTN_DELETE,
                    Buttons.BTN_DISTRIBUTE,
                }; // buttons
                temp = temp2;
            } // else
            buttons = temp;
        } // else

        // return button array
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Show the quickview of the object
     *
     * @param   representationForm  Kind of representation.
     */
    public void quickView (int representationForm)
    {
        // show the properties of the object:
        this.showQuickView (representationForm);
    } // show


    /**************************************************************************
     * Show the object's quickView
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showQuickView (int representationForm)
    {
        if (true)                       // business object resists on this
                                        // server?
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the object:
                retrieve (Operations.OP_READ);
*/
                if (this.getUserInfo ().userProfile.showRef)
                {
                    this.performRetrieveRefs (Operations.OP_VIEW);
                } // if

                // show the object's data:
                this.performShowQuickView (representationForm);
            } // try
            catch (NoAccessException e) // no access to objects allowed
            {
                // send message to the user:
                this.showNoAccessMessage (Operations.OP_READ);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e) // no access to objects allowed
            {
                // send message to the user:
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if container object resists on this server
        else                            // object resists on another server
        {
            // invoke the object on the other server:
        } // else object resists on another server
    } // show


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user.
     * <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    private void performShowQuickView (int representationForm)
    {
        Page page = new Page (MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_BUSINESSOBJECT, env), false);

/* ********
Stylesheets
********** */

        // create the stylesheets
        StyleSheetElement stylesheet = new StyleSheetElement ();

        // buttons
        stylesheet = new StyleSheetElement ();
        stylesheet.importSS = this.sess.activeLayout.path +
            this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.BUTTONBAR].styleSheet;
        page.head.addElement (stylesheet);
        stylesheet = new StyleSheetElement ();
        stylesheet.importSS = this.sess.activeLayout.path +
            this.env.getBrowser () + "/" +
            this.sess.activeLayout.elems[LayoutConstants.SHEETINFO].styleSheet;

        // add the stylesheet to the page
        page.head.addElement (stylesheet);

        // set the icon of this object:
        this.setIcon ();

        // create Header
        GroupElement body = this.createHeader (page, this.name, null, null,
            null, this.icon, this.containerName);
        TableElement table = new TableElement (3);
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.border = 0;
//        table.classId = CssConstants.CLASS_INFO;

        RowElement tr;
        if ((this.rights & Operations.OP_NEW) == Operations.OP_NEW)
        {
            tr = new RowElement (2);
        } // if
        else
        {
            tr = new RowElement (1);
        } // else

//        TableDataElement td = new TableDataElement (Helpers.getTextField (content));
        TableElement table2 = this.createFrame (representationForm);

        table2.classId = CssConstants.CLASS_INFO;
        String[] classIds = new String[2];
        classIds[0] = CssConstants.CLASS_NAME;
        classIds[1] = CssConstants.CLASS_VALUE;
        table2.classIds = classIds;

        this.showProperties (table2);
        TableDataElement td = new TableDataElement (table2);

        td.width = HtmlConstants.TAV_FULLWIDTH;
        tr.addElement (td);

        if ((this.level < 3) &&
            ((this.rights & (Operations.OP_NEW | Operations.OP_ADDELEM)) ==
            (Operations.OP_NEW | Operations.OP_ADDELEM)))
        {
            // the answer button

            // create script for showing the button and add it to a new
            // cell within the current table row:
            ScriptElement script = new ScriptElement (ScriptElement.LANG_JAVASCRIPT);
            script.addScript ("top.showSingleButton ('" + this.oid + "', " +
                Buttons.BTN_ANSWER + ", document)");
            tr.addElement (new TableDataElement (script));
            tr.addElement (new TableDataElement (new BlankElement (3)));
        } // if

        table.addElement (tr);

        if (this.getUserInfo ().userProfile.showRef)
        {
            this.showRefs (table, page);
        } // if

        body.addElement (table);

        // build the page and show it to the user:
        try
        {
            page.build (this.env);
        } // try
        catch (BuildException e)
        {
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
    } // performShowQuickView


    /**************************************************************************
     * Get the ids of all types which are allowed to be contained within the
     * actual object type. <BR/>
     * This method depends on the value of the oid and gets the corresponding
     * object type out of the object pool. Then it gets the types which may be
     * contained within this type out of the type itself.
     *
     * @return  An array with the allowed type ids.
     */
    public String[] getTypeIds ()
    {
        String[] typeIds = null;        // the type ids for the object type

        // check if there is a valid oid:
        if (this.oid != null)           // oid exists?
        {
            // get the discussion:
            XMLDiscussion_01 disc = (XMLDiscussion_01)
                BOHelpers.getObject (this.discussionId, this.env, false, false);

            // check if we got the discussion:
            if (disc != null)       // got discussion?
            {
                // get the discussiontemplateoid and initialize another object with it
                XMLDiscussionTemplate_01 discTemp = (XMLDiscussionTemplate_01)
                    BOHelpers.getObject (disc.refOid, this.env, false, false);

                // check if we got the object:
                if (discTemp != null) // got discussion template?
                {
                    // get the oids of templates for each level
                    OID tempOid = null;

                    switch (this.level)
                    {
                        case 1:
                            // level 1 ... get the second level as template for an answer
                            tempOid = discTemp.level2;
                            break;
                        case 2:
                            // level 2 ... get the third level as template for an answer
                            tempOid = discTemp.level3;
                            break;
                        default:
                            // nothing to do
                    } // get the right templateOid for the level

                    // retrieve the type of the first level template
                    // get the type of the first level template:
                    if (this.level > 0 && this.level < 3)
                    {
                        typeIds = new String[1];
                        typeIds[0] = "" + tempOid.tVersionId;
//                        typeIds[0] = getTypeInfo (tempOid);
                    } // if
                } // if got discussion template
            } // if got discussion
        } // if oid exists

        return typeIds;                 // return the computed type ids
    } // getTypeIds

} // class DiscXMLViewer_01

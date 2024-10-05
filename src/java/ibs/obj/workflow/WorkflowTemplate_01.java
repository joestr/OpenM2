/*
 * Class: WorkflowTemplate_01.java
 */

// package:
package ibs.obj.workflow;

// import:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.File_01;
import ibs.service.user.User;
import ibs.service.workflow.WorkflowMessages;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class represents one object of type WorkflowTemplate with version 01.
 * A WorkflowTemplate object holds an XML file that serves as a template for new
 * Workflow types. WorkflowTemplates act like file objects that store single
 * files. XMLViewer objects read these XML files to build their Workflow structure
 * upon the definiton in the XML file. <BR/>
 *
 * @version     $Id: WorkflowTemplate_01.java,v 1.12 2010/05/20 07:59:00 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowTemplate_01 extends File_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowTemplate_01.java,v 1.12 2010/05/20 07:59:00 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class WorkflowTemplate_01.
     * <BR/>
     */
    public WorkflowTemplate_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // WorkflowTemplate_01


    /**************************************************************************
     * Creates a WorkflowTemplate_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public WorkflowTemplate_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // WorkflowTemplate_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();    // has same specifics as super class

        // set as master attachment:
        this.isMaster = true;
    } // initClassSpecifics


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
        // check if this template is referenced
        // by any active workflows: if there
        // are any references no 'delete' button
        // will be displayed
        if (this.isReferencedByActiveWorkflows ())
        {
            // define buttons to be displayed:
            int[] buttons =
            {
                Buttons.BTN_EDIT,
//                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_COPY,
                Buttons.BTN_DISTRIBUTE,
                Buttons.BTN_SEARCH,
            }; // buttons
            // return button array
            return buttons;
        } // if

        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
        }; // buttons

        // return button array:
        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Represent the object, i.e. its properties, to the user within a form;
     * first a check will be made if this template is used in an active workflow;
     * if yes: warning-message.* <BR/>
     * Overwrites super-method; super-method will be called at the end of this
     * method.
     *
     * @param   representationForm  Kind of representation.
     * @param   function            The function which shall be called when
     *                              the user clicks "OK".
     */
    protected void performShowChangeForm (int representationForm, int function)
    {
        // check if this template is referenced by any active workflows
        if (this.isReferencedByActiveWorkflows ())
        {
            IOHelpers.showMessage (
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_WARNING_TEMPLATEINUSE, env),
                this.app, this.sess, this.env);
        } // if

        // call super:
        super.performShowChangeForm (representationForm, function);
    } // performShowChangeForm


    /**************************************************************************
     * Represent the properties of a Dokument_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
//        showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_HIDDEN, "" + false);
        // incl. pathname = oid of container
        this.showFormProperty (table, BOArguments.ARG_FILE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env),
            Datatypes.DT_FILE, this.fileName, "" + this.containerId);
    } // showFormProperties


    /**************************************************************************
     * Checks if this template is referenced by active workflows. <BR/>
     *
     * @return  true    this template is referenced by active workflows
     *          false   otherwise
     */
    private boolean isReferencedByActiveWorkflows ()
    {
        // check if this workflow-template is used by an active workflow
        // get info from view v_WorkflowTemplate_01$ref

        // init variables
        int rowCount = 0;
        int numRows = 0;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select the user from the ibs_User table by his/her name;
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        String queryStr = " SELECT COUNT(*) AS numRows " +
                          " FROM v_WorkflowTemplate_01$ref " +
                          " WHERE definitionId = " + this.oid.toStringQu ();

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // empty resultset or error
            if (rowCount != 1)
            {
                numRows = 0;
            } // if
            else
            {
                // get tuple out of db
                if (!action.getEOF ())
                {
                    numRows = action.getInt ("numRows");
                } // if
            } // else
        } // try
        catch (DBError e)
        {
            // get all errors (can be chained):
            String allErrors = "";
            String h = new String (e.getMessage ());
            h += e.getError ();
            // loop through all errors and concatenate them to a String:
            while (h != null)
            {
                allErrors += h;
                h = e.getError ();
            } // while
            // show the message
            IOHelpers.showMessage (allErrors,
                this.app, this.sess, this.env);
            // init counter-value
            numRows = 0;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // check if any references found
        if (numRows == 0)
        {
            return false;
        } // if

        return true;
    } // isReferencedByActiveWorkflows

} // class WorkflowTemplate_01

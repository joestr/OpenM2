/*
 * Class: ServicePoint_01.java
 */

// package:
package ibs.service.servicepoint;

// imports:
//KR TODO: unsauber
import ibs.bo.BOArguments;
//KR TODO: unsauber
import ibs.bo.BOConstants;
//KR TODO: unsauber
import ibs.bo.BOTokens;
//KR TODO: unsauber
import ibs.bo.Buttons;
//KR TODO: unsauber
import ibs.bo.Datatypes;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DIArguments;
import ibs.di.DIMessages;
import ibs.di.DITokens;
import ibs.di.XMLViewerContainer_01;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.TableElement;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * HACK: overwrites XMLViewerContainer_01
 *
 * @version     $Id: ServicePoint_01.java,v 1.10 2010/05/20 07:59:00 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 21112000
 *
 * @deprecated  This class is never used.
 ******************************************************************************
 */
public class ServicePoint_01 extends XMLViewerContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ServicePoint_01.java,v 1.10 2010/05/20 07:59:00 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class ServicePoint_01.
     * <BR/>
     */
    public ServicePoint_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ServicePoint_01


    /**************************************************************************
     * This constructor creates a new instance of the class ServicePoint_01. <BR/>
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
     */
    public ServicePoint_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // ServicePoint_01


    /**************************************************************************
     * Overwrites the show method.
     *
     * @param   representationForm  Kind of representation.
     */
    public void show (int representationForm)
    {
        // show the properties of the object:
        this.showInfo (representationForm);
    } // show


    /**************************************************************************
     * Represent the properties of a ServicePoint_01 object to the user. <BR/>
     * Overwrites super - removed not needed elements.
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
//showDebug ("--- START showProperties ---");

/////////////////////////////////
//
// --> super.super.showProperties
//
        // loop through all properties of this object and display them:
        this.showProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showProperty (table, BOArguments.ARG_TYPE, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_TYPE, env),
            Datatypes.DT_TYPE, this.getMlTypeName ());
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        this.showProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION, this.description);
        this.showProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

        if (this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            this.showProperty (table, null, null, Datatypes.DT_SEPARATOR,
                (String) null);
            this.showProperty (table, BOArguments.ARG_OWNER, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_OWNER, env),
                Datatypes.DT_USER, this.owner);
            this.showProperty (table, BOArguments.ARG_CREATED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CREATED, env),
                Datatypes.DT_USERDATE, this.creator, this.creationDate);
            this.showProperty (table, BOArguments.ARG_CHANGED, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHANGED, env),
                Datatypes.DT_USERDATE, this.changer, this.lastChanged);
        } // if (app.userInfo.userProfile.showExtendedAttributes)

        this.showProperty (table, null, null, Datatypes.DT_SEPARATOR, (String) null);
//
//
/////////////////////////////////

        // check if we have a template
        if (!this.p_templateObj.oid.isEmpty ())
        {
            // template
            this.showProperty (table, DIArguments.ARG_TEMPLATE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_TEMPLATE, env),
                Datatypes.DT_TEXT, this.p_templateObj.name);
        } // if (this.templateFileName != null && !this.templateFileName.equals (""))
        else
        {   // no template available
            // show a message that there are no templates
            this.showProperty (table, DIArguments.ARG_TEMPLATE,
                MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                    DITokens.ML_TEMPLATE, env),
                Datatypes.DT_TEXT,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_NOTEMPLATESAVAILABLE, env));
        } // else no template available

        // workflow template
        this.showProperty (table, DIArguments.ARG_WORKFLOWTEMPLATE,
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_WORKFLOWTEMPLATE, env),
            Datatypes.DT_TEXT, this.workflowTemplateName);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a ServicePoint_01 object to the user
     * within a form. <BR/>
     * Overwrites super - removed not needed elements.
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
//showDebug ("--- START showFormProperties ---");

/////////////////////////////////
//
// --> super.super.showFormProperties
//
        // property 'name':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        // loop through all properties of this object and display them:
        this.showFormProperty (table, BOArguments.ARG_NAME, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env),
            Datatypes.DT_NAME, this.name);
        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // restrict: empty entries allowed
        this.formFieldRestriction =
            new FormFieldRestriction (true, BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION,
            this.description);
        // property 'validUntil':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: no empty entries allowed
        // 0 .. default size/length values for datatype will be taken
        // null .. no upper bound
        this.formFieldRestriction =
            new FormFieldRestriction (false);
        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);
//
//
/////////////////////////////////

        GroupElement gel;
        GroupElement wfgel;

        gel = this.createTemplatesSelectionBox (DIArguments.ARG_TEMPLATE);
        this.showFormProperty (table,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_TEMPLATE, env), gel);

        // workflow template
        wfgel = this.createWorkflowTemplatesSelectionBox (
            DIArguments.ARG_WORKFLOWTEMPLATE, this.workflowTemplateName, false);
        this.showFormProperty (table,    
            MultilingualTextProvider.getText (DITokens.TOK_BUNDLE, 
                DITokens.ML_WORKFLOWTEMPLATE, env), wfgel);
    } // showFormProperties


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * --> CONTENT will never be viewed for this object
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons sepp


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
//            Buttons.BTN_NEW,
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
            Buttons.BTN_CUT,
            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

} // class ServicePoint_01

/*
 * Class: Url_01.java
 */

// package:
package ibs.obj.doc;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOTokens;
import ibs.bo.Datatypes;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.Attachment_01;
import ibs.obj.doc.DocConstants;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.util.FormFieldRestriction;


/******************************************************************************
 * This class represents one object of type Attachment with version 01. <BR/>
 * Attachments represent relationships between objects and files or webpages.
 * An object can have multiple attachments. In case multiple attachments have been
 * assigned one must be set as master which means that these master assignments
 * will be displayed when the content of an objects is viewed by the user.
 * Within a set of attachments there can only be one master attachment. <BR/>
 * This version of attachment does not support compound documents. (files that
 * includes other files like HTML files). <BR/>
 *
 * @version     $Id: Url_01.java,v 1.23 2010/05/20 07:59:00 btatzmann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ) 981006
 ******************************************************************************
 */
public class Url_01 extends Attachment_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Url_01.java,v 1.23 2010/05/20 07:59:00 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class Url_01. <BR/>
     */
    public Url_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Url_01


    /**************************************************************************
     * Creates a Url_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Url_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Url_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();
        this.attachmentType = DocConstants.ATT_HYPERLINK;
        this.isMaster = false;
        this.searchExtended = false;
    } // initClassSpecifics


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        super.getParameters ();

        this.attachmentType = DocConstants.ATT_HYPERLINK;
        this.isMaster = false;
    } // getParameters


    /**************************************************************************
     * Get a parameter which is a file. <BR/>
     */
    protected void getFileParameter ()
    {
        // nothing to do
    } // getFileParameter


//KR TODO: The function hasFile returns true. Maybe this is wrong.
    /**************************************************************************
     * Returns if the Object is a file-type. <BR/>
     * For URLs this is <CODE>true</CODE>.
     *
     * @return  <CODE>true</CODE> if the object contains a file field,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean hasFile ()
    {
        return true;
    } // hasFile


    /**************************************************************************
     * Represent the properties of a Url_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties should be added.
     */
    protected void showProperties (TableElement table)
    {
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

        if (this.checkedOut)
        {
            if (this.checkOutUserName != null &&
                !this.checkOutUserName.isEmpty ())
            {
                this.showProperty (table, BOArguments.ARG_NOARG,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHECKEDOUT, env), Datatypes.DT_USERDATE,
                    this.checkOutUser, this.checkOutDate);
            } // if
            else
            {
                this.showProperty (table, BOArguments.ARG_NOARG,
                    MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_CHECKEDOUT, env), Datatypes.DT_DATETIME,
                    this.checkOutDate);
            } // else
        } // if

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
        // is it a internal weblink?
        boolean isInternalWeblink = this.checkIsInternalWeblink (this.url);

        this.showProperty (table, BOArguments.ARG_HYPERLINK, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env),
            Datatypes.DT_URL, this.url, this.isWeblink, isInternalWeblink);
    } // showProperties


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
        this.showFormProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);

        // property 'description':
        // set form field restriction (will automatically be used in
        // next showFormProperty)
        // restrict: empty entries allowed, maximum lenght is MAX_LENGTH_DESCRIPTION
        //           (actually (30.01.2001) it is 255)
        this.formFieldRestriction = new FormFieldRestriction (true,
            BOConstants.MAX_LENGTH_DESCRIPTION, 0);
        this.showFormProperty (table, BOArguments.ARG_DESCRIPTION,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_DESCRIPTION, env), Datatypes.DT_DESCRIPTION,
            this.description);

        this.showFormProperty (table, BOArguments.ARG_VALIDUNTIL,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_VALIDUNTIL, env), Datatypes.DT_DATE, this.validUntil);

//debug("URL_SFPType"+ this.attachmentType  + "Url"+ this.url );
        this.showFormProperty (table, BOArguments.ARG_HYPERLINK,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env), Datatypes.DT_URL, this.url);
    } // showFormProperties


    /**************************************************************************
     * Is the object type allowed in workflows? <BR/>
     * This method shall be overwritten in subclasses.
     *
     * @return  <CODE>true</CODE> if the object type is allowed in workflows,
     *          <CODE>false</CODE> otherwise.
     */
    protected boolean isWfAllowed ()
    {
        return true;
    } // isWfAllowed

} // class Url_01

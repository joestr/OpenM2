/*
 * Class: UserAddress_01.java
 */

// package:
package ibs.obj.user;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Datatypes;
import ibs.di.DataElement;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.UserArguments;
import ibs.obj.user.UserTokens;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one object of type ibs.bo.BusinesObject
 * version 01. <BR/>
 *
 * @version     $Id: UserAddress_01.java,v 1.10 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Koban Ferdinand (FF), 010116
 ******************************************************************************
 */
public class UserAddress_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserAddress_01.java,v 1.10 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     * emailaddress of the current user (for notification). <BR/>
     */
    protected String eMailAddress = "";

    /**
     * smsemailaddress of the current user (for notification). <BR/>
     * must be registered by an email service
     */
    protected String smsEMail = "";


    /**************************************************************************
     * This constructor creates a new instance of the class. <BR/>
     */
    public UserAddress_01 ()
    {
        // call constructor of super class:
        super ();
    } // UserAddress_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the class-procedureNames:
        this.procCreate   = "p_UserAddress_01$create";
        this.procChange   = "p_UserAddress_01$change";
        this.procRetrieve = "p_UserAddress_01$retrieve";
        this.procDelete   = "p_UserAddress_01$delete";

        // set number of parameters for procedure calls:
        this.specificRetrieveParameters = 2;
        this.specificChangeParameters = 2;
    } // initClassSpecifics


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // set the specific parameters:
        // eMailAddress
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.eMailAddress);
        // smsEMail
        sp.addInParameter (ParameterConstants.TYPE_STRING,
                        this.smsEMail);
    } // setSpecificChangeParameters


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
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // eMail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // smsEMail
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param params        The array of parameters from the retrieve data stored
     *                   procedure.
     * @param lastIndex The index to the last element used in params thus far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.eMailAddress = params[++i].getValueString ();
        this.smsEMail = params[++i].getValueString ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Gets the parameters which are relevant for this object. <BR/>
     * The <A HREF="#env">env</A> property is used for getting the parameters.
     * This property must be set before calling this method. <BR/>
     */
    public void getParameters ()
    {
        String text = "";

        super.getParameters ();

        // get eMail
        if ((text = this.env
            .getParam (UserArguments.ARG_NOTIFICATION_EMAILADRESS)) != null)
        {
            this.eMailAddress = text;
        } // if

        // get smsEMail
        if ((text = this.env
            .getParam (UserArguments.ARG_NOTIFICATION_SMSADRESS)) != null)
        {
            this.smsEMail = text;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a UserAddress_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showProperties (table);
        // eMailAddress
        this.showProperty (table,
                      UserArguments.ARG_NOTIFICATION_EMAILADRESS,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOTIFICATION_EMAILADRESS, env),
                      Datatypes.DT_EMAIL,
                      this.eMailAddress);
        // smseMailAddress
        this.showProperty (table,
                      UserArguments.ARG_NOTIFICATION_SMSADRESS,
                      MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOTIFICATION_SMSEMAILADRESS, env),
                      Datatypes.DT_EMAIL,
                      this.smsEMail);
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Person_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // eMail-address
        this.showFormProperty (table,
                          UserArguments.ARG_NOTIFICATION_EMAILADRESS,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOTIFICATION_EMAILADRESS, env),
                          Datatypes.DT_EMAIL,
                          this.eMailAddress);

        // smsEMail-address
        this.showFormProperty (table,
                          UserArguments.ARG_NOTIFICATION_SMSADRESS,
                          MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, UserTokens.ML_NOTIFICATION_SMSEMAILADRESS, env),
                          Datatypes.DT_EMAIL,
                          this.smsEMail);
    } // showFormProperties



    //
    // import / export methods
    //
    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values:
        super.readImportData (dataElement);
        // get the type specific values:
        // email
        if (dataElement.exists ("eMail"))
        {
            this.eMailAddress =
                dataElement.getImportStringValue ("eMailAddress");
        } // if
        // smsemail
        if (dataElement.exists ("smsEMail"))
        {
            this.smsEMail = dataElement.getImportStringValue ("smsEMail");
        } // if
    } // readImportData


    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values:
        super.writeExportData (dataElement);
        // set the type specific values:
        // email
        dataElement.setExportEmailValue ("eMail", this.eMailAddress);
        // smsEmail
        dataElement.setExportEmailValue ("smsEMail", this.smsEMail);
    } // writeExportData

} // class UserAddress_01

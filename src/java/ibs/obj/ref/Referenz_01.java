/*
 * Class: Referenz_01.java
 */

// package:
package ibs.obj.ref;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.di.KeyMapper;
import ibs.di.RTExceptionInvalidLink;
import ibs.di.ValueDataElement;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.TableElement;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.StoredProcedure;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: Referenz_01.java,v 1.11 2013/01/16 16:14:14 btatzmann Exp $
 *
 * @author      Klaus Reimüller (KR), 980428
 *
 * @see     ibs.bo.BusinessObject
 ******************************************************************************
 */
public class Referenz_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Referenz_01.java,v 1.11 2013/01/16 16:14:14 btatzmann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class Referenz_01. <BR/>
     */
    public Referenz_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // Referenz_01


    /**************************************************************************
     * Creates a Referenz_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public Referenz_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // Referenz_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.procCreate =  "p_Referenz_01$create";
        this.procChange = "p_Referenz_01$change";

        // set number of parameters for procedure calls:
        this.specificChangeParameters = 1;
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
        // set specific parameters of the object
        super.setSpecificChangeParameters (sp);

        // add specific parameters
        // linkedObjectId
        sp.addInParameter (ParameterConstants.TYPE_STRING, (this.linkedObjectId != null) ?
            this.linkedObjectId.toString () : "");
    } // setSpecificChangeParameters


    /**************************************************************************
     * Represent the properties of a Referenz_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showProperties (TableElement table)
    {
        // display the base object's properties:
        super.showProperties (table);
        // loop through all properties of this object and display them:
     //   showProperty (table, AppArguments.ARG_VALIDUNTIL, TOK_VALIDUNTIL, Datatypes.DT_DATE, Helpers.dateToString (validUntil));
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Referenz_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     */
    protected void showFormProperties (TableElement table)
    {
        // display the base object's properties:
        super.showFormProperties (table);
        // loop through all properties of this object and display them:
      //   showFormProperty (table, AppArguments.ARG_VALIDUNTIL, TOK_VALIDUNTIL, Datatypes.DT_DATE, Helpers.dateToString (validUntil));
    } // showFormProperties


    /**************************************************************************
     * Reads the object data from an DataElement. <BR/>
     *
     * @param dataElement     the DataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        // get business object specific values
        super.readImportData (dataElement);

        // get the reference value
        ValueDataElement value = dataElement.getValueElement ("reference");
        if (value != null)
        {
            // get the id and domain from the reference value (EXTKEX)
            String id = value.value;
            String domain = value.p_domain;
            // instantiate the Keymapp to resolve the external object key
            KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);
            // try to resolve the external key
            this.linkedObjectId = keyMapper.performResolveMapping (
                new KeyMapper.ExternalKey (domain, id));
        } // if reference value found

        // check if the reference was imported successfully
        if (this.linkedObjectId == null)
        {
            // Sorry! But there is no other possibility to report errors.
            // workaround!!!
            throw new RTExceptionInvalidLink (MultilingualTextProvider
                .getMessage(BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_OBJECTNOTFOUND, env));
        } // if linked object not found
    } // readImportData


    /**************************************************************************
     * Writes the object data to an DataElement. <BR/>
     *
     * @param dataElement     the DataElement to write the data to
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);

        // export the external key of the referenced object
        KeyMapper keyMapper = new KeyMapper (this.user, this.env, this.sess, this.app);
        // get the EXTKEY for the referenced object.
        KeyMapper.ExternalKey extKey = keyMapper.performResolveMapping (this.linkedObjectId, true);
        if (extKey != null)
        {
            // export the external key of the linked object
            dataElement.setExportExtKeyValue ("reference", extKey.getId (), extKey.getDomain ());
        } // if the EXTKEY is valid
    } // writeExportData

} // class Referenz_01

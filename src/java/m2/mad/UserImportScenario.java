/*
 * Class: UserImportScenario.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.type.TypeConstants;
import ibs.di.DIConstants;
import ibs.di.DIMessages;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.imp.ImportScenario;
import ibs.di.imp.ImportScriptElement;
import ibs.di.imp.ImportScript_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.user.Group_01;
import ibs.obj.user.User_01;
import ibs.service.user.User;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import m2.mad.Address_01;
import m2.mad.Company_01;
import m2.mad.Person_01;
import m2.mad.MadTypeConstants;

import java.util.Enumeration;
import java.util.Vector;


/******************************************************************************
 * The userImportScenario handles the import of user data. It creates
 * a company object with a person object within. It creates a user and all
 * groups the user belongs to. The person will be linked to the user and the
 * user to his groups. <BR/>
 * In case there in not all the data present, the userImport will perform the
 * following steps:
 * <UL>
 * <LI>try to create the company object
 * <LI>try to change the companys address in case the company has been created
 * <LI> try to create a person. in case a company has been created, the person
 * will be created within the companys contacts tab. in case there is no company
 * present, try to create the person within a container defined in the importScript
 * <LI>try to create a user. in case there has been a person created attach the person
 * to the user
 * <LI>in case there has been a user created try to create the group memberships.
 * </UL>
 *
 * @version     $Id: UserImportScenario.java,v 1.34 2010/12/23 13:08:24 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 991013
 ******************************************************************************
 */
public class UserImportScenario extends ImportScenario
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: UserImportScenario.java,v 1.34 2010/12/23 13:08:24 rburgermann Exp $";


    /**************************************************************************
     * Creates an UserImportScenario Object. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    @Deprecated
    public UserImportScenario (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Integrator_01


    /**************************************************************************
     * Creates an UserImportScenario Object. <BR/>
     */
    public UserImportScenario ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Integrator_01


    /**************************************************************************
     * Processes the user import. <BR/>
     *
     * @param dataElementList       the dataElementList object containing the data
     * @param importScript          the importScript that holds control data
     * @param importContainerOid    the OID of the container the import has
     *                              been started from
     * @param importOperation       operation for the import                              
     *
     * @return true if the processing has been successful or false otherwise
     */
    public boolean process (DataElementList dataElementList,
                            ImportScript_01 importScript,
                            OID importContainerOid,
                            String importOperation)
    {
        // create an object factory
        this.objectFactory = this.getObjectFactory ();

        // create CN specific user structures
        Company_01 company = null;
//        Address_01 address = null;
//        Address_01 pAddress = null;
        Person_01 person = null;
        User_01 user = null;

        // now create the user and set personOid to oid of the person we created
        // the container id of the person
        // get the data element
        DataElement elemUser =
            dataElementList.findTypename (this.getTypeCache ().getTypeName (TypeConstants.TC_User));
        if (elemUser == null)
        {
            elemUser = dataElementList.findTypecode (TypeConstants.TC_User);
        } // if (elemUser == null)
        // get the import script entry
        ImportScriptElement scriptUser =
            importScript.find (this.getTypeCache ().getTypeName (TypeConstants.TC_User),
                TypeConstants.TC_User);
        // check if we found the data
        if (elemUser != null && scriptUser != null)
        {
            if (scriptUser.operationType != DIConstants.OPERATION_NONE)
            {
                // get the container oid
                OID userContainerOid = importScript.getContainerFromType (scriptUser, importContainerOid);
                // create the user
                user = this.processUser (elemUser, userContainerOid);
            } // if (scriptUser.operationType != DIConstants.OPERATION_NONE)
        } // if (elemUser != null && scriptUser != null)

        // now create the relations between the user and all specified groups
        // that means loop through all group sections in the DataElementList
        // only do that if a user exists
        if (user != null)
        {
            Vector<DataElement> groupsVector = dataElementList.findMultiple (this
                .getTypeCache ().getTypeName (TypeConstants.TC_Group));
            ImportScriptElement scriptGroup =
                importScript.find (this.getTypeCache ().getTypeName (TypeConstants.TC_Group),
                    TypeConstants.TC_Group);
            // check if we found the data
            if (groupsVector != null && scriptGroup != null)
            {
                if (scriptGroup.operationType != DIConstants.OPERATION_NONE)
                {
                    // get the container oid
                    OID groupContainerOid = importScript.getContainerFromType (
                        scriptGroup, importContainerOid);
                    // process the group data
                    this.processGroups (user.oid, user.memberShip,
                        groupsVector, groupContainerOid);
                } // if (scriptGroup.operationType != DIConstants.OPERATION_NONE)
            } // if (groupsVector != null && scriptGroup != null)
        } // if (user != null)
        // try to create a company object
        // get the data element
        DataElement elemCompany =
            dataElementList.findTypename (this.getTypeCache ().getTypeName (MadTypeConstants.TC_Company));
        if (elemCompany == null)
        {
            elemCompany = dataElementList.findTypecode (MadTypeConstants.TC_Company);
        } // if (elemCompany == null)
        // get the import script entry
        ImportScriptElement scriptCompany =
            importScript.find (this.getTypeCache ().getTypeName (MadTypeConstants.TC_Company),
                MadTypeConstants.TC_Company);
        // check if we found the data
        if (elemCompany != null && scriptCompany != null)
        {
            if (scriptCompany.operationType != DIConstants.OPERATION_NONE)
            {
                // get the containerOid
                OID companyContainerOid = importScript.getContainerFromType (scriptCompany, importContainerOid);
                // process the company data
                company = this.processCompany (elemCompany, companyContainerOid);
            } // if (scriptCompany.operationType != DIConstants.OPERATION_NONE)
        } // if (elemCompany != null)

        // the company now has an address object - just change it
        // take the oid from the company object
        // no key mapping require because address exists when company exists
        // check first if the company has been created
        DataElement elemAddress = null;
        if (company != null)
        {
            elemAddress = dataElementList.findTypename (this.getTypeCache ().getTypeName (TypeConstants.TC_Address));
            if (elemAddress == null)
            {
                elemAddress = dataElementList.findTypecode (TypeConstants.TC_Address);
            } // if (elemAddress == null)
            // check if we have an address
            if (elemAddress != null)
            {
//                address =
                this.processAddress (company.tabAddress, elemAddress);
            } // if (company != null)
        } // if (company != null)

        // now create the person within the company
        // in case there is no company present try to create the person
        // in an container specified in the importScript
        // get the data element
        DataElement elemPerson =
            dataElementList.findTypename (this.getTypeCache ().getTypeName (TypeConstants.TC_Person));
        if (elemPerson == null)
        {
            elemPerson = dataElementList.findTypecode (TypeConstants.TC_Person);
        } // if (elemPerson == null)
        // check if we found the data
        if (elemPerson != null)
        {
            // get the oid of the user the person will reference
            OID userOid = null;
            if (user != null)
            {
                userOid = user.oid;
            } // if
            // check if there has been a company created
            if (company != null)
            {
                // create the person within the company
                person = this.processPerson (company.tabContacts, elemPerson, userOid);
            } // if (company != null)
            else    //  try to create the the person in an container
            {
                // get the import script entry
                ImportScriptElement scriptPerson =
                    importScript.find (this.getTypeCache ().getTypeName (TypeConstants.TC_Person),
                        TypeConstants.TC_Person);
                // first check if there is an entry in the importScript
                if (scriptPerson != null)
                {
                    // get the container oid
                    OID personContainerOid = importScript.getContainerFromType (scriptPerson, importContainerOid);
                    // create the person within an container specified in the importScript
                    person = this.processPerson (personContainerOid, elemPerson, userOid);
                } // if (scriptPerson != null)
            } // else create the the person in an container
        } // if (elemPerson != null)

        // the person now has an address object - just change it
        // this is tricky because there is no distinction between an company
        // address object and a person address object. therefore we look for
        // a "Person Adresse" object type otherwise take the address object
        // from the company
        // only create the address if the person has been created before
        if (person != null)
        {
            // try to find the person address with the type name
            // if this was not possible try to find it with type code
            DataElement elemPAddress = dataElementList.findTypename (this
                .getTypeCache ().getTypeName (TypeConstants.TC_Person) +
                " " +
                this.getTypeCache ().getTypeName (TypeConstants.TC_Address));
            if (elemPAddress == null)
            {
                // type code is here "Person Address"
                elemPAddress = dataElementList
                    .findTypecode (TypeConstants.TC_Person + " " +
                        TypeConstants.TC_Address);
            } // if (dataElement.p_typeCode != null)
            if (elemPAddress == null)
            {
                // the person address is the same as the company address
                // check if there is an address object of the company available
                if (elemAddress != null)
                {
//                    pAddress =
                    this.processAddress (person.tabAddress, elemAddress);
                } // if
            } // if (elemPAddress != null)
//            pAddress =
            this.processAddress (person.tabAddress, elemPAddress);
        } // if (person != null)

        // BB HINT: there is no distinction yet when this import scenario failed
        // or succeeded therefore return always null
        return true;
    } // process


    /**************************************************************************
     * Create the company object. <BR/>
     * Checks first if the company object already exists by resolving a
     * key mapping.
     *
     * @param elemCompany           the dataElement that holds the company data
     * @param companyContainerOid   the oid of the container the company shall be
     *                              created in
     *
     * @return the company object or null otherwise
     */
    private Company_01 processCompany (DataElement elemCompany, OID companyContainerOid)
    {
        // create CN specific user structures:
        Company_01 company = null;
        boolean isChange;

        if (companyContainerOid == null)
        {
            // log that object could not have been created
            this.log.add (DIConstants.LOG_ERROR,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALID_CONTAINERID, env));
            this.log.add (DIConstants.LOG_ERROR, elemCompany.oid,
                elemCompany.typename + " '" + elemCompany.name + "' " +
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (containerOid == null)

        // try to resolve the key mapping
        OID oidCompany = this.objectFactory.getKeyMapper (elemCompany.id, elemCompany.idDomain);
        // check if we could get the oid
        if ((oidCompany != null) && !oidCompany.isEmpty ())
        {
            isChange = true;
            // get the company object
            company = (m2.mad.Company_01) this.objectFactory.getObject (oidCompany, this.user, this.sess, this.env);
        } // if (oidCompany != null)
        else
        {   // create the company object
            isChange = false;
            company = (m2.mad.Company_01) this.objectFactory
                .getObjectFromType (this.getTypeCache ().getTypeId (
                    MadTypeConstants.TC_Company));
            company.containerId = companyContainerOid;
            company = (m2.mad.Company_01) this.objectFactory.performCreateObject (company);
            // create a key mapping
            this.objectFactory.createKeyMapper (company.oid, elemCompany.id, elemCompany.idDomain);
        } // else create the company object
        if (company != null)
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the company data first
                company.retrieve (Operations.OP_CHANGE);
*/
                // read in the data of the object from the DataElement
                company.readImportData (elemCompany);
                // now save the changes to the object:
                company.performChange (Operations.OP_CHANGE);
                if (isChange)
                {
                    this.log.add (DIConstants.LOG_ENTRY, company.oid,
                        elemCompany.typename + " '" + elemCompany.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCHANGED, env));
                } // if
                else
                {
                    this.log.add (DIConstants.LOG_ENTRY, company.oid,
                        elemCompany.typename + " '" + elemCompany.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCREATED, env));
                } // else
            } // try
            catch (NoAccessException e)
            {
                this.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
            catch (NameAlreadyGivenException e)
            {
/* KR: just for debugging purposes
log.add (DIConstants.LOG_ENTRY, company.oid,
    "NameAlreadyGivenException in company");
*/
                // BB HINT: the showNameAlreadyGivenMessage method creates a java script
                // alert and is therefore not suitable for our purposes
                // the alert would interrrupt the import log
//                showNameAlreadyGivenMessage ();
                // we construct
                String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {company.name}, env);
                IOHelpers.showMessage (message,
                    this.app, this.sess, this.env);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e)
            {
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // else object init
        // show the status bar
        this.showStatusBar ();
        // check if the company could be created
        if (company == null)
        {
            this.log.add (DIConstants.LOG_ERROR, null,
                elemCompany.typename + " '" + elemCompany.name + "' " + 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTPROCESSOBJECT, env));
        } // if (company == null)
        return company;
    } // createCompany


    /**************************************************************************
     * The company now has an adress object - just change it.
     * Take the oid from the company object.
     * No key mapping require because address exists when company exists. <BR/>
     *
     * @param addressOid    the oid of the address object (coming from the company object)
     * @param elemAddress   the dataElement that holds the address data
     *
     * @return the address object or null otherwise
     */
    private Address_01 processAddress (OID addressOid, DataElement elemAddress)
    {

        // create CN specific user structures
        Address_01 address = null;
        // get the address object
        address = (Address_01) this.objectFactory.getObject (addressOid,
            this.user, this.sess, this.env);
        try
        {
/* KR 020125: not necessary because already done before
            // try to retrieve the address data first
            address.retrieve (Operations.OP_CHANGE);
*/
            // read in the data of the object from the DataElement
            address.readImportData (elemAddress);
            // the name of the tab object must be set to the title of the tab
            // else it will not be recognized as tab object!!!
            address.name = this.getTypeCache ().getTypeName (TypeConstants.TC_Address);
            // save the changes to the object:
            address.performChange (Operations.OP_CHANGE);
            this.log.add (DIConstants.LOG_ENTRY, address.oid,
                elemAddress.typename + " '" + elemAddress.name + "' " + 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_OBJECTCHANGED, env));
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
        } // catch
        catch (NameAlreadyGivenException e)
        {
/* KR: just for debugging purposes
log.add (DIConstants.LOG_ENTRY, address.oid,
    "NameAlreadyGivenException in address");
*/
            // BB HINT: the showNameAlreadyGivenMessage method creates a java script
            // alert and is therefore not suitable for our purposes
            // the alert would interrupt the import log
//                showNameAlreadyGivenMessage ();
            // we construct
            String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {address.name}, env);
            IOHelpers.showMessage (message,
                this.app, this.sess, this.env);
        } // catch
/* KR 020125: not necessary because already done before
        catch (AlreadyDeletedException e)
        {
            showAlreadyDeletedMessage ();
        } // catch
*/
        this.showStatusBar ();
        return address;
    } // createAddress


    /**************************************************************************
     * The company now has an adress object - just change it.
     * Take the oid from the company object.
     * No key mapping require because address exists when company exists. <BR/>
     *
     * @param contactsOid   the oid of the contacts tab of the company
     * @param elemPerson    the dataElement that holds the company data
     * @param userOid       the oid of the user to be referenced
     *
     * @return the person object or null otherwise
     */
    private Person_01 processPerson (OID contactsOid, DataElement elemPerson,
                                     OID userOid)
    {
        // create CN specific user structures
        Person_01 person = null;
        boolean isChange;

        if (contactsOid == null || userOid == null)
        {
            // log that object could not have been created
            this.log.add (DIConstants.LOG_ERROR,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALID_CONTAINERID, env));
            this.log.add (DIConstants.LOG_ERROR, userOid,
                elemPerson.typename + " '" + elemPerson.name + "' " + 
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (containerOid == null)
        // try to resolve the key mapping
        OID oidPerson = this.objectFactory.getKeyMapper (elemPerson.id, elemPerson.idDomain);
        // check if we could get the oid
        if ((oidPerson != null) && (!oidPerson.isEmpty ()))
        {
            isChange = true;
            // get the person object
            person = (m2.mad.Person_01) this.objectFactory.getObject (oidPerson, this.user, this.sess, this.env);
        } // if (oidPerson != null)
        else
        {   // create the person object
            isChange = false;
            person = (m2.mad.Person_01) this.objectFactory.getObjectFromType (this.getTypeCache ().getTypeId (TypeConstants.TC_Person));
            // set the the company contacts oid as containerId
            person.containerId = contactsOid;
            // create the object now
            person = (m2.mad.Person_01) this.objectFactory.performCreateObject (person);
            // create a key mapping
            this.objectFactory.createKeyMapper (person.oid, elemPerson.id, elemPerson.idDomain);
        } // else create the person object
        if (person != null)
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the person data first
                person.retrieve (Operations.OP_CHANGE);
*/
                // read in the date of the object from the DataElement
                person.readImportData (elemPerson);
                // set the reference to the user
                person.userOid = userOid;
                // now save the changes to the object:
                person.performChange (Operations.OP_CHANGE);
                if (isChange)
                {
                    this.log.add (DIConstants.LOG_ENTRY, person.oid,
                        elemPerson.typename + " '" + elemPerson.name + "' " + 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCHANGED, env));
                } // if
                else
                {
                    this.log.add (DIConstants.LOG_ENTRY, person.oid,
                        elemPerson.typename + " '" + elemPerson.name + "' " + 
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCREATED, env));
                } // else
            } // try
            catch (NoAccessException e)
            {
                this.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
            catch (NameAlreadyGivenException e)
            {
/* KR: just for debugging purposes
log.add (DIConstants.LOG_ENTRY, person.oid,
    "NameAlreadyGivenException in person");
*/
                // BB HINT: the showNameAlreadyGivenMessage method creates a java script
                // alert and is therefore not suitable for our purposes
                // the alert would interrrupt the import log
//                showNameAlreadyGivenMessage ();
                // we construct
                String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {person.name}, env);
                IOHelpers.showMessage (message,
                    this.app, this.sess, this.env);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e)
            {
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if (person != null)
        // show the status bar
        this.showStatusBar ();
        // check if the person could be created
        if (person == null)
        {
            this.log.add (DIConstants.LOG_ERROR, null, elemPerson.typename +
                " '" + elemPerson.name + "' " +  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTPROCESSOBJECT, env));
        } // if (person == null)
        return person;
    } // processPerson


    /**************************************************************************
     * Create a user and a link between the user and the corresponding
     * person. <BR/>
     *
     * @param elemUser          the dataElement that holds the user data
     * @param userContainerOid  the oid of the container the user will be
     *                          created in
     *
     * @return the user object or null otherwise
     */
    private User_01 processUser (DataElement elemUser, OID userContainerOid)
    {
        User_01 user = null;
        boolean isChange;

        if (userContainerOid == null)
        {
            // log that object could not have been created
            this.log.add (DIConstants.LOG_ERROR,  
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_INVALID_CONTAINERID, env));
            this.log.add (DIConstants.LOG_ERROR, elemUser.oid,
                elemUser.typename + " '" + elemUser.name + "' " +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
            return null;
        } // if (containerOid == null)

        // try to resolve the key mapping
        OID oidUser = this.objectFactory.getKeyMapper (elemUser.id, elemUser.idDomain);
        // check if we could get the oid
        if ((oidUser != null) && !oidUser.isEmpty ())
        {
            isChange = true;
            // get the object
            user = (User_01) this.objectFactory.getObject (oidUser, this.user, this.sess, this.env);
        } // if (oidUser != null)
        else
        {   // create the User object
            isChange = false;
            // get the user object
            user = (User_01) this.objectFactory.getObjectFromType (this.getTypeCache ().getTypeId (TypeConstants.TC_User));
            // set the container oid
            user.containerId = userContainerOid;
            user = (User_01) this.objectFactory.performCreateObject (user);
            // create a key mapping
            this.objectFactory.createKeyMapper (user.oid, elemUser.id, elemUser.idDomain);
        } // else create the user object
        if (user != null)
        {
            try
            {
/* KR 020125: not necessary because already done before
                // try to retrieve the user data first
                user.retrieve (Operations.OP_CHANGE);
*/
                // read in the date of the object from the DataElement
                user.readImportData (elemUser);
                // now save the changes to the object:
                user.performChange (Operations.OP_CHANGE);
                if (isChange)
                {
                    this.log.add (DIConstants.LOG_ENTRY, user.oid,
                        elemUser.typename + " '" + elemUser.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCHANGED, env));
                } // if (isChange)
                else    // not changed but created
                {
                    this.log.add (DIConstants.LOG_ENTRY, user.oid,
                        elemUser.typename + " '" + elemUser.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_OBJECTCREATED, env));
                } // else not changed but created
            } // try
            catch (NoAccessException e)
            {
                this.showNoAccessMessage (Operations.OP_CHANGE);
            } // catch
            catch (NameAlreadyGivenException e)
            {
/* KR: just for debugging purposes
log.add (DIConstants.LOG_ENTRY, user.oid,
    "NameAlreadyGivenException in user");
*/
                // BB HINT: the showNameAlreadyGivenMessage method creates a java script
                // alert and is therefore not suitable for our purposes
                // the alert would interrrupt the import log
//                showNameAlreadyGivenMessage ();
                // we construct
                String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {user.name}, env);
                IOHelpers.showMessage (message,
                    this.app, this.sess, this.env);
            } // catch
/* KR 020125: not necessary because already done before
            catch (AlreadyDeletedException e)
            {
                showAlreadyDeletedMessage ();
            } // catch
*/
        } // if (user != null)
        // show the status bar
        this.showStatusBar ();
        // check if the user could be created
        if (user == null)
        {
            this.log.add (DIConstants.LOG_ERROR, null, elemUser.typename +
                " '" + elemUser.name + "' " +
                MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                    DIMessages.ML_MSG_COULDNOTPROCESSOBJECT, env));
        } // if (user == null)
        return user;
    } // processUser


    /**************************************************************************
     * create the user and a link between the user and the corresponding
     * person. <BR/>
     *
     * @param userOid           the oid of the user
     * @param userMemberShip    the oid of the users membership tab
     * @param groupsVector      the vector with the groups
     * @param groupContainerOid the oid of the container a group will be
     *                          created in
     */
    private void processGroups (OID userOid, OID userMemberShip,
                                Vector<DataElement> groupsVector,
                                OID groupContainerOid)
    {
        Group_01 group;

        // check if there are any elements in the groups vector
        if (groupsVector.size () > 0)
        {
            String[] groupIds = new String[groupsVector.size ()];
            DataElement elemGroup;
            Enumeration<DataElement> groupEnum = groupsVector.elements ();
            int i = 0;
            while (groupEnum.hasMoreElements ())
            {
                elemGroup = groupEnum.nextElement ();

                if (groupContainerOid == null)
                {
                    // log that object could not have been created
                    this.log.add (DIConstants.LOG_ERROR,  
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_INVALID_CONTAINERID, env));
                    this.log.add (DIConstants.LOG_ERROR, elemGroup.oid,
                        elemGroup.typename + " '" + elemGroup.name + "' " +
                        MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                            DIMessages.ML_MSG_COULDNOTCREATEOBJECT, env));
                } // if (containerOid == null)
                // try to resolve the key mapping
                OID oidGroup = this.objectFactory.getKeyMapper (elemGroup.id, elemGroup.idDomain);
                // check if we could get the oid
                if ((oidGroup == null) || oidGroup.isEmpty ())
                {
                    // create the group object
                    group = (Group_01) this.objectFactory.getObjectFromType (this.getTypeCache ().getTypeId (TypeConstants.TC_Group));
                    // set the container id
                    group.containerId = groupContainerOid;
                    // create the group
                    group = (Group_01) this.objectFactory.performCreateObject (group);
                    // create 1a key mapping
                    this.objectFactory.createKeyMapper (group.oid, elemGroup.id, elemGroup.idDomain);
                    // now save the changes to the object:
                    try
                    {
/* KR 020125: not necessary because already done before
                        // try to retrieve the group data first
                        group.retrieve (Operations.OP_CHANGE);
*/
                        // read in the date of the object from the DataElement
                        group.readImportData (elemGroup);
                        // now change the group
                        group.performChange (Operations.OP_CHANGE);
                        this.log.add (DIConstants.LOG_ENTRY, group.oid,
                            elemGroup.typename + " '" + elemGroup.name + "' " +
                            MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                                DIMessages.ML_MSG_OBJECTCREATED, env));
                    } // try
                    catch (NoAccessException e)
                    {
                        this.showNoAccessMessage (Operations.OP_CHANGE);
                    } // catch
                    catch (NameAlreadyGivenException e)
                    {
/* KR: just for debugging purposes
log.add (DIConstants.LOG_ENTRY, group.oid,
    "NameAlreadyGivenException in processGroups: " + elemGroup.id);
*/
                        // BB HINT: the showNameAlreadyGivenMessage method creates a java script
                        // alert and is therefore not suitable for our purposes
                        // the alert would interrrupt the import log
//                        showNameAlreadyGivenMessage ();
                        // we construct
                        String message = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_NAMEALREADYGIVEN, new String[] {group.name}, env);
                        IOHelpers.showMessage (message,
                            this.app, this.sess, this.env);
                    } // catch
/* KR 020125: not necessary because already done before
                    catch (AlreadyDeletedException e)
                    {
                        showAlreadyDeletedMessage ();
                    } // catch
*/
                    // set the group OID
                    oidGroup = group.oid;
                    // show the status bar
                    this.showStatusBar ();
                } //  if (oidGroup == null)
                // add the group oid to the array
                groupIds[i++] = oidGroup.toString ();
            } // while
            // create the group user reletionships
            if (this.createRelationshipToGroups (userOid, groupIds))
            {
                this.log.add (DIConstants.LOG_ENTRY,  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_GROUP_MEMBERSHIPS_CREATED, env));
            } // if
            else                        // an error occurred
            {
                this.log.add (DIConstants.LOG_WARNING,  
                    MultilingualTextProvider.getMessage (DIMessages.MSG_BUNDLE,
                        DIMessages.ML_MSG_GROUP_MEMBERSHIPS_FAILED, env));
            } // else
            // show the status bar
            this.showStatusBar ();
        } // if (groupsVector.size > 0)
    } // create groups


    /**************************************************************************
     * Create the relationships between a user and the groups he belongs
     * to. <BR/>
     *
     * @param   userOid      oid of the user to link the groups to
     * @param   oidStrings   oid`s of groups to be joined
     *
     * @return  <CODE>true</CODE> if the stored procedure call was successful;
     *          <CODE>false</CODE> otherwise.
     */
    private boolean createRelationshipToGroups (OID userOid, String[] oidStrings)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_User_01$setGroups",
            StoredProcedureConstants.RETURN_VALUE);
        int retVal = UtilConstants.QRY_OK; // return value of query
        OID oid = null;                 // the actual oid
        OID emptyOid = null;

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // input parameters:
        // user oid
        BOHelpers.addInParameter (sp, userOid);
        // create empty oid:
        emptyOid = OID.getEmptyOid ();

        // add the groupOids as parameters
        for (int k = 0; k < 15; k++)
        {
            if (k < oidStrings.length)  // oid exists?
            {
                try
                {
                    oid = new OID (oidStrings [k]);
                } // try
                catch (IncorrectOidException e)
                {
                    oid = emptyOid;
                } // catch
            } // if oid exists
            else                        // no valid oid
            {
                oid = emptyOid;
            } // else no valid oid
            // add the parameter
            BOHelpers.addInParameter (sp, oid);
        } // for (int k = 0; k < 15; k ++)

        // execute stored procedure
        try
        {
            // perform the function call:
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            retVal = UtilConstants.QRY_NOT_OK;
        } // catch

        // return if stored procedure was successful
        return retVal == UtilConstants.QRY_OK;
    } // createRelationshipToGroups


    /**************************************************************************
     * Shows a debug message. <BR/>
     *
     * @param message   debug  message to be shown
     *
     * @deprecated  This method is not longer necessary. Instead the IDE
     *              debugging mechanism shall be used. All calls to this method
     *              shall be deleted.
     */
    @Deprecated
    public void showDebug (String message)
    {
        if (false)
        {
            // check if an environment is available
            if (this.env != null)
            {
                this.env.write ("<DIV ALIGN=\"LEFT\">" + this.getClass ().getName () + ":" +
                          message + "</DIV><P>");
            } //if
            else
            {
                System.out.println (this.getClass ().getName () + ":" + message);
            } // else
        } // if (true)
    } // showDebug


    /***************************************************************************
     * This method is called at the start of the import. <BR/>
     * Overwrite this method in the corresponding subclasses to perform an
     * action. <BR/>
     */
    public void executionOnStart ()
    {
        // nothing to do
    } // executionOnStart


    /***************************************************************************
     * This method is called at the end of the import. <BR/>
     * Overwrite this method in the corresponding subclasses to perform an
     * action. <BR/>
     */
    public void executionOnEnd ()
    {
        // nothing to do
    } // executionOnEnd

} // UserImportScenario

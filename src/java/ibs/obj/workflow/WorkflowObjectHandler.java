/*
 * Class: WorkflowObjectHandler.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.bo.type.TypeConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.ref.Referenz_01;
import ibs.obj.wsp.Recipient_01;
import ibs.obj.wsp.SentObject_01;
import ibs.service.user.User;
import ibs.service.workflow.Variable;
import ibs.service.workflow.Variables;
import ibs.service.workflow.WorkflowConstants;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * Holds methods to create/handle/modify objects used during workflow-activies.
 * <BR/>
 *
 * @version     $Id: WorkflowObjectHandler.java,v 1.29 2013/01/17 15:21:42 btatzmann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowObjectHandler extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowObjectHandler.java,v 1.29 2013/01/17 15:21:42 btatzmann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    //
    // attributes
    //

    /**
     * WorkflowRightsHandler: does operations on rights. <BR/>
     */
    public WorkflowRightsHandler rightsHandler;


    ///////////////////////////////////////////////////////////////////////////
    //
    // constructors
    //

    /**************************************************************************
     * Creates an WorkflowDefinition Object. <BR/>
     */
    public WorkflowObjectHandler ()
    {
        // nothing to do
    } // constructor


    ///////////////////////////////////////////////////////////////////////////
    //
    // methods for object creation
    //

    /**************************************************************************
     * Retrieves m2-object. <BR/>
     *
     * @param oid   the oid of the object to retrieve
     * @param user  user for fetch-operation
     *
     * @return      the m2-object
     *              <CODE>null</CODE> if no success
     */
    public BusinessObject fetchObject (OID oid, User user)
    {
        // get a new worklow-object:
        BusinessObject obj = BOHelpers.getObject (
            oid, this.env.getApplicationInfo (), this.env.getSessionInfo (),
            user, this.env, false, true, true);


        // exit method
        return obj;
    } // fetchObject


    /**************************************************************************
     * Instantiates a new m2-object of type Workflow_01. <BR/>
     *
     * @return      The object of type Workflow_01
     *              <CODE>null</CODE> if no success
     */
    public Workflow_01 fetchNewWorkflowObject ()
    {
        // init object
        Workflow_01 workflow = null;

        // get a new worklow-object:
        workflow = (Workflow_01)
            BOHelpers.getNewObject (TypeConstants.TC_Workflow, this.env);

        // exit method
        return workflow;
    } // fetchNewWorkflowObject


    /**************************************************************************
     * Fetch existing WorkflowTemplate_01 object identified by given name. <BR/>
     *
     * @param   templateName    The name of the workflow-template to retrieve.
     * @param   user            User for fetch-operation.
     *
     * @return  The object of type WorkflowTemplate_01
     *          <CODE>null</CODE> if no success (not found or duplicate entries)
     */
    public WorkflowTemplate_01 fetchWorkflowTemplateByName (String templateName,
                                                    User user)
    {
        int rowCount = 0;
        OID templateOid = null;
        WorkflowTemplate_01 templateObj = null;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select the templates oid from the ibs_Attachment
        // ensure that object is
        // - active (in ibs_Object)
/* BB: this does not ensure unique results!
        String queryStr = " SELECT o.oid " +
                          " FROM ibs_Object o, ibs_Attachment_01 a" +
                          " WHERE a.oid = o.oid " +
                          " AND o.name = '" + templateName + "'" +
                          " AND o.state = " + States.ST_ACTIVE;
*/
        String queryStr = " SELECT o.oid " +
                          " FROM ibs_Object o" +
                          " WHERE o.tVersionId = '" + this.getTypeCache ()
                              .getTVersionId (TypeConstants.TC_WorkflowTemplate) + "'" +
                          " AND o.name = N'" + templateName + "'" +
                          " AND o.state = " + States.ST_ACTIVE +
                          " AND o.isLink = " + SQLConstants.BOOL_FALSE;

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
                IOHelpers.showMessage (
                    "Error in fetchWorkflowTemplateByName: Non-unique name = " + templateName,
                    this.env);
                return null;            // terminate this method
            } // if

            // get tuple out of db
            if (!action.getEOF ())
            {
                templateOid = SQLHelpers.getQuOidValue (action, "oid");
            } // if

        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            return null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // get and retrieve object from given oid;
        // user MUST have rights to retrieve object
        templateObj = (WorkflowTemplate_01) this.fetchObject (templateOid, user);

        // exit
        return templateObj;
    } // fetchWorkflowTemplateByName


    /*************************************************************************
     * Create a new workflow instance Workflow_01. <BR/>
     * The created workflow-instance Workflow_01 connects a given workflow
     * template (Attachment_01) to a given BusinessObject. The new object
     * instance is persistent (stored in db).
     *
     * @param   obj         The BusinessObject which shall be connected.
     * @param   template    The template for the new workflow instance.
     *
     * @return  The new workflow instance object
     *          (<CODE>null</CODE> if creation failed).
     */
    protected Workflow_01 createWorkflowInstance (BusinessObject obj,
                                                  WorkflowTemplate_01 template)
    {
        // initialize local variables
        Workflow_01 workflow = null;

        // create a new workflow-instance object
        if ((workflow = this.fetchNewWorkflowObject ()) != null)
                            // got object?
        {
            // create and initialize workflow-instance object
            workflow.name = "Workflow (" + obj.name + ")";     // set default name
            workflow.containerId = obj.oid;     // store under forward-object

            // create object - creates object in db with state 'CREATED'
            // (0 = this.representationform; very old unused structure)
//            workflow.setOid (workflow.create (0));
            workflow.setOid (workflow.forceCreate ());

            // set initial attributes
            workflow.objectId = obj.oid;
            workflow.definitionId = template.oid;
            workflow.startDate = new Date ();
            workflow.workflowState = WorkflowConstants.STATE_OPEN_NOTRUNNING_NOTSTARTED;
            workflow.currentStateName = WorkflowConstants.STATE_UNDEFINED;
            workflow.processManager = 0;
            workflow.starter = this.user.id;
            workflow.currentOwner = 0;
            workflow.writeLog = true;

            try
            {
                workflow.processManagerCont = OID.getEmptyOid ();
                workflow.starterContainer =
                    new OID (obj.containerId.toString ());
            } // try
            catch (IncorrectOidException e)
            {
                // ignore, because constant OID_NOOBJECT is always a valid OID
            } // catch

            // last but not least:
            // store the attributes of the new object in the DB
            // sets state to 'ACTIVE'
            try
            {
                // check for no-rights!! (normaly OP_CHANGE)
                workflow.performChange (0);
            } // try
            catch (NoAccessException e) // no access to object allowed
            {
                workflow = null;
                obj.showNoAccessMessage (0);
            } // catch
            catch (ibs.util.NameAlreadyGivenException e) // no access to object allowed
            {
                // not necessary, but compiler wants it!
            } // catch

        } // if (workflow != null)

        return workflow;
    } // createWorkflowInstance


    /**************************************************************************
     * Create a link to the forwarded object. <BR/>
     *
     * @param refContainerId    id of container where reference resides
     * @param refObjectId       id of object to reference
     * @param theUser           user for whom the link will be created
     *
     * @return  <CODE>true</CODE>    reference created
     *          <CODE>false</CODE>   otherwise
     */
    protected boolean createReference (OID  refContainerId,
                                   OID  refObjectId,
                                   User theUser)
    {
        // init local variables
        boolean retValue = false;       // return value; init
        BusinessObject refObj;
        OID refOid = null;
        OID newObjectOid = null;

        // create a new reference:
        refOid = OID.getTempOid (this.getTypeCache ().getTVersionId (TypeConstants.TC_Reference));

        // create new reference object
        refObj = new Referenz_01 (refOid, theUser);

        // check if object could be created
        if (refObj != null) // object ready?
        {
            // new reference object was created
            // now initialize it
            refObj.initObject (refOid, theUser, this.env, this.sess, this.app);
            refObj.setEnv (this.env);   // set environment
            refObj.sess = this.sess;    // get application object

            // set all values necessary of the reference object
            // set containerId as the oid of the target
            refObj.containerId = refContainerId;
            refObj.isLink = true;
            refObj.isContainer = false;
            refObj.linkedObjectId = refObjectId;
            refObj.name = this.getTypeCache ().getTypeName (TypeConstants.TC_Reference);

            // create the new object in the database
            // second parameter: no rights-check necessary
            newObjectOid = refObj.createActive (0, 0);
        } // if

        // check success
        if (newObjectOid != null)
        {
            retValue = true;
        } // if

        // everything ok exit
        return retValue;
    } // createReference


////////////////////////
//
// the core of this method is a copy of BusinessObject.move
// - except type checking
//
    /**************************************************************************
     * moves the given object to the given target. <BR/>
     *
     * @param   targetId    Oid of the target container.
     * @param   obj         The object for wich the references shall be created.
     *
     * @return  <CODE>true</CODE>, <CODE>false</CODE>.
     */
    protected boolean moveObject (OID targetId, BusinessObject obj)
    {
        // PROBLEM: no success-check possible!

        // update the the objectstructure from the database:
        try
        {
            // try to move within the database:
            // (operation = 0, no rights needed for workflow-move)
            obj.performMoveData (targetId, 0); // not defined
        } // try
        catch (NoAccessException e) // no access to object allowed
        {
            // cannot occur!
        } // catch

        // exit
        return true;
    } // moveObject


    /**************************************************************************
     * Create an entry in the inbox of the new owner. <BR/>
     *
     * @param   obj         The forwarded business object.
     * @param   userOid     OID of the new owner.
     * @param   subject     Subject of message.
     * @param   description Description of the message.
     * @param   activities  Activities attached to the message.
     *
     * @return  The OID of the sentObject created.
     *
     * @deprecated  This method does not work correctly. It should be similar to
     *              {@link ibs.service.notification.NotificationService#objectDistribute(OID, OID, String, String, String, boolean, boolean)
     *              ibs.service.notification.NotificationService.objectDistribute}
     *              If necessary please update this method to the corresponding
     *              functionality.
     */
    @Deprecated
    public OID createInboxEntry (BusinessObject obj,
                                 OID userOid,
                                 String subject,
                                 String description,
                                 String activities)
    {
        Recipient_01 recipient = null;
        SentObject_01 sentObject = null;
        OID outboxOid = null;
        OID receiverEntryOid = null;
        OID sentObjectOid = null;
        OID recipientOid = null;

        // create a sentobject entry in the outbox:
        sentObjectOid = new OID (this.getTypeCache ().getTVersionId (TypeConstants.TC_SentObject), 0);
        sentObject = new SentObject_01 (sentObjectOid, this.user);
        if (sentObject != null)
        {
            sentObject.initObject (sentObjectOid, this.user, this.env, this.sess, this.app);
            sentObject.setEnv (this.env); // set environment
            sentObject.sess = this.sess; // get application object
            // create an empty oid for the containerId
            outboxOid = OID.getEmptyOid ();
            // set type within oid:
            sentObject.type = this.getTypeCache ().getTVersionId (TypeConstants.TC_SentObject);
            // set container id:
            sentObject.containerId = outboxOid;
            // set properties of outbox object
            sentObject.name = subject;
            sentObject.description = description;
            sentObject.distributeId = obj.oid;
            // name of object will be will be added in stored procedure
            sentObject.distributeType = obj.type;
            sentObject.distributeTypeName = obj.typeName;
            sentObject.activities = activities;
            sentObject.freeze = false;

            // create the new object
            sentObjectOid = sentObject.store (0);
            if (sentObjectOid != null) // object was created, oid returned?
            {
                // create the recipient object:
                receiverEntryOid = userOid;
                recipientOid = new OID (this.getTypeCache ().getTVersionId (TypeConstants.TC_Recipient), 0);
                recipient = new Recipient_01 (recipientOid, this.user);
                if (recipient != null)
                {
                    recipient.initObject (recipientOid, this.user, this.env,
                        this.sess, this.app);
                    recipient.setEnv (this.env); // set environment
                    recipient.sess = this.sess; // get application object
                    // set type within oid:
                    recipient.oid.type = recipient.type;
                    // type version id of recipient_01
                    recipient.type = this.getTypeCache ().getTVersionId (TypeConstants.TC_Recipient);
                    // dummy value
                    // should be fullname of recipient name
                    recipient.name = sentObject.name;
                    // set container id:
                    // will be redirected in the stored procedure
                    // to the related recipients container
                    recipient.containerId = outboxOid;
                    recipient.setIsTab (false);
                    recipient.description = description;
                    // create an entry in the outbox receivers list
                    recipient.recipientId = receiverEntryOid;
                    recipient.sentObjectId = sentObjectOid;
                    // make object persistent in the db
                    // store performs a create (performCreateData)
                    // and a change (performChangeData)
                    recipientOid = recipient.store (0);

                    if (recipientOid == null) // object not created?
                    {
                        // show corresponding message:
                        IOHelpers.showMessage (MultilingualTextProvider
                            .getMessage(BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_OBJECTNOTCREATED, this.env),
                            this.app, this.sess, this.env);
                    } // if object not created
                } // if
                else                            // didn't get object
                {
                    IOHelpers.showMessage (MultilingualTextProvider
                        .getMessage(BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                        this.app, this.sess, this.env);
                } // else
            } // if object was created, oid returned
            else                    // object not created
            {
                // show corresponding message:
                IOHelpers.showMessage (MultilingualTextProvider
                        .getMessage(BOMessages.MSG_BUNDLE,
                        BOMessages.ML_MSG_OBJECTNOTCREATED, this.env),
                    this.app, this.sess, this.env);
            } // else object not created
        } // if
        else                            // didn't get object
        {
            IOHelpers.showMessage (MultilingualTextProvider
                    .getMessage(BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                this.app, this.sess, this.env);
        } // else

        // return the oid of the sentobject created or null
        return sentObjectOid;
    } // createInboxEntry


    /**************************************************************************
     * Gets the workflow-flag of the given BusinessObject. <BR/>
     *
     * @param   objectId    id of object to add rights
     *
     * @return      setting of businessobjects workflow-flag (true|false)
     */
    public boolean getWorkflowFlag (OID objectId)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_Workflow$getWorkflowFlag",
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // objectId
        BOHelpers.addInParameter (sp, objectId);

        // output parameters:
        // value
        Parameter valueParam = sp.addOutParameter (ParameterConstants.TYPE_BOOLEAN);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);

        // return the flag value
        return valueParam.getValueBoolean ();
    } // getWorkflowFlag


    /**************************************************************************
     * Sets the workflow-flag of the given BusinessObject. <BR/>
     *
     * @param   objectId    id of object to add rights
     * @param   value       setting of businessobjects workflow-flag (true|false)
     */
    public void setWorkflowFlag (OID objectId, boolean value)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_Workflow$setWorkflowFlag",
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters
        // objectId
        BOHelpers.addInParameter (sp, objectId);
        // value
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, value);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);
    } // setWorkflowFlag


    /**************************************************************************
     * Get a user from the DB by its name. <BR/>
     *
     * @param userName   name of the user
     *                   null if user does not exist or duplicate entries
     *
     * @return  The user object,
     *          <CODE>null</CODE> if the user was not found.
     */
    protected User getUserFromName (String userName)
    {
        int rowCount = 0;
        User theUser = null;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // assert that a username is available
        if (userName == null || userName.isEmpty ())
        {
            return null;
        } // if

        // select the user from the ibs_User table by his/her name;
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        String queryStr = " SELECT u.id, u.oid, u.domainId, u.password, u.fullname " +
                          " FROM ibs_User u, ibs_Object o " +
                          " WHERE u.name = '" + userName + "' " +
                          " AND u.oid = o.oid " +
                          " AND o.state = " + States.ST_ACTIVE +
                          " AND u.domainId = " + this.user.domain;

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
                return null;            // terminate this method
            } // if

            // create user object
            theUser = new User (userName);

            // get tuple out of db
            if (!action.getEOF ())
            {
                theUser.id = action.getInt ("id");
                theUser.oid = SQLHelpers.getQuOidValue (action, "oid");
                theUser.domain = action.getInt ("domainId");
                theUser.password = action.getString ("password");
                theUser.fullname = action.getString ("fullname");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            theUser = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return theUser;
    } // getUserFromName


    /**************************************************************************
     * Get a user from the DB by its id (int). <BR/>
     *
     * @param userId    id of the user
     *
     * @return  The user object.
     *          <CODE>null</CODE> if user does not exist or duplicate entries.
     */
    protected User getUserFromId (int userId)
    {
        int rowCount = 0;
        User theUser = null;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select the user from the ibs_User table by his/her name
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        String queryStr = " SELECT u.id, u.oid, u.domainId, u.password, u.fullname, u.name " +
                          " FROM ibs_User u, ibs_Object o " +
                          " WHERE u.id = " + userId + " " +
                          " AND u.oid = o.oid " +
                          " AND o.state = " + States.ST_ACTIVE +
                          " AND u.domainId = " + this.user.domain;

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
                return null;            // terminate this method
            } // if

            // create user object
            theUser = new User ("");

            // get tuple out of db
            if (!action.getEOF ())
            {
                theUser.id = userId;
                theUser.username = action.getString ("name");
                theUser.oid = SQLHelpers.getQuOidValue (action, "oid");
                theUser.domain = action.getInt ("domainId");
                theUser.password = action.getString ("password");
                theUser.fullname = action.getString ("fullname");
            } // if
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            theUser = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return theUser;
    } // getUserFromId


    /**************************************************************************
     * Retrieve oid for each user-name in given list. <BR/>
     * names of users that do not exists (no user, deleted, ...)
     * are stored in the second parameter userNamesNonExistent)
     *
     * @param   userNames               List of user-names.
     * @param   userNamesNonExistent    List of user-names of users not found
     *                                  in system.
     *
     * @return  List of user-oids; empty if no users given or found
     *          <CODE>null</CODE> if an error occurred.
     */
    protected Vector<OID> getUserOidsByNames (
                                              Vector<String> userNames,
                                              Vector<String> userNamesNonExistent)
    {
        Vector<OID> userOIDs = new Vector<OID> ();
        Vector<String> userNamesFromDB = new Vector<String> ();
        SQLAction action = null;        // the action object used to access the
                                        // database

        // build WHERE-clause for SELECT query
        StringBuilder whereClause = new StringBuilder ("");
        String elem;

        for (Iterator<String> iter = userNames.iterator (); iter.hasNext ();)
        {
            // get next element:
            elem = iter.next ();

            // check if valid:
            if (elem != null)
            {
                // add to clause
                whereClause.append ("u.name = '").append (elem).append ("'");
            } // if

            if (iter.hasNext ())
            {
                whereClause.append (" OR ");
            } // if
        } // for iter

        // select user-oids from the ibs_User table by his/her name
        // ensure that he/she is
        // - in the same domain
        // - active (in ibs_Object)
        String queryStr = " SELECT u.oid, u.name " +
                          " FROM ibs_User u, ibs_Object o " +
                          " WHERE (" + whereClause.toString () + ")" +
                          " AND u.oid = o.oid " +
                          " AND o.state = " + States.ST_ACTIVE +
                          " AND u.domainId = " + this.user.domain;

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            action.execute (queryStr, false);

            // get tuple out of db
            while (!action.getEOF ())
            {
                // add to vectors
                userOIDs.addElement (SQLHelpers.getQuOidValue (action, "oid"));
                userNamesFromDB.addElement (action.getString ("name"));
                // switch to next row
                action.next ();
            } // if
        } // try
        catch (DBError err)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (err, this.app, this.sess, this.env, false);
            userOIDs = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // compare entries of given userNames with entries of
        // retrieved userNamesFromDB; store not-retrieved names
        // in userNamesNonExistent
        String u1;
        String u2;
        boolean found;

        for (Iterator<String> iter1 = userNames.iterator (); iter1.hasNext ();)
        {
            // get next name:
            u1 = iter1.next ();

            // try to find it in other list:
            found = false;

            for (Iterator<String> iter2 = userNamesFromDB.iterator ();
                iter2.hasNext ();)
            {
                // get next name:
                u2 = iter2.next ();

                // compare the names:
                if (u1.equalsIgnoreCase (u2))
                {
                    // entry found
                    found = true;
                    break;
                } // if
            } // for iter2

            // if not found in other list add to userNamesNonExistent
            if (!found)
            {
                userNamesNonExistent.addElement (u1);
            } // if
        } // for iter1

        return userOIDs;
    } // getUserOidsByNames


    /**************************************************************************
     * Writes a protocol entry. <BR/>
     *
     * @param   instanceId          The workflow instance.
     * @param   objectId            The id of the object (forwarded).
     * @param   objectName          The name of the object (forwarded).
     * @param   currentState        Name of the current state.
     * @param   operationType       Type of operations.
     * @param   fromParticipant     User which performs operation.
     * @param   toParticipant       User which recieves object.
     * @param   additionalComment   Some additional comments (errors, ...).
     */
    public void createProtocolEntry (OID instanceId,
                                     OID objectId,
                                     String objectName,
                                     String currentState,
                                     int operationType,
                                     User fromParticipant,
                                     User toParticipant,
                                     String additionalComment)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_WorkflowProtocol$createEntry",
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // instance id
        BOHelpers.addInParameter (sp, instanceId);
        // objectId
        BOHelpers.addInParameter (sp, objectId);
        // objectName
        sp.addInParameter (ParameterConstants.TYPE_STRING, objectName);
        // currentState
        sp.addInParameter (ParameterConstants.TYPE_STRING, currentState);
        // operationType
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, operationType);
        // fromParticipantId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, fromParticipant.id);
        // toParticipantId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, toParticipant.id);
        // fromParticipantName
        sp.addInParameter (
            ParameterConstants.TYPE_STRING, fromParticipant.fullname);
        // toParticipantName
        sp.addInParameter (
            ParameterConstants.TYPE_STRING, toParticipant.fullname);
        // additional comments
        sp.addInParameter (ParameterConstants.TYPE_STRING, additionalComment);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);
    } // createProtocolEntry


    /***************************************************************************
     * Retreives the system user and returns a User-object. <BR/> The system
     * user executes all the workflow-service methods. For now the system user
     * is always the domains administrator.
     *
     * @param domainId the domain for which the system-user shall be retrieved
     *
     * @return the User object of the system-user null if user does not exist or
     *         duplicate entries
     */
    public User getSystemUser (int domainId)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_Workflow$getSystemUserId",
            StoredProcedureConstants.RETURN_NOTHING);
        int userId = 0;

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // domainId
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, domainId);

        // output parameters:
        // users id
        Parameter userIdParam = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);

        // get value of output parameter: userId
        userId = userIdParam.getValueInteger ();

        // check success
        if (userId < 0)
        {
            return null;
        } // if

        // return user-object
        return this.getUserFromId (userId);
    } // getSystemUser


    /**************************************************************************
     * Deletes all entries in table ibs_objectRead for given object, including
     * references on this object. <BR/>
     *
     * @param   objectId    the oid of the object
     */
    public void delObjReadEntries (OID objectId)
    {
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_Workflow$delObjReadEntries",
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // objectId
        BOHelpers.addInParameter (sp, objectId);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);
    } // delObjReadEntries


    /**************************************************************************
     * Retrieve all variables stored for given instance and store them in the
     * given variable-object. <BR/> Remark: only name and value will be set
     * in variables; type, description and length are not stored in DB.
     *
     * @param workflowInstance      the workflow instance
     *
     * @return  The variables object; holds all retrieved variables.
     *          <CODE>null</CODE> if an error occured.
     */
    protected Variables getVariablesOfInstance (Workflow_01 workflowInstance)
    {
        // init variables
        int rowCount = 0;
        Variables variables = new Variables ();
        Variable variable;
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select variables for given instance
        String queryStr = " SELECT v.variableName, v.variableValue " +
                          " FROM ibs_WorkflowVariables v " +
                          " WHERE v.instanceId = " +
            workflowInstance.oid.toStringQu ();

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);

            // check rowcount
            if (rowCount == 0)
            {
                // no - variables set return empty variables-object
                return variables;
            } // if ...

            // get tuple out of db
            while (!action.getEOF ())
            {
                // create new variable entry
                variable = new Variable (action.getString ("variableName"),
                    WorkflowConstants.UNDEFINED, WorkflowConstants.UNDEFINED,
                    WorkflowConstants.UNDEFINED,
                    action.getString ("variableValue"));

                // add variable
                variables.addEntry (variable);

                // switch to next row
                action.next ();
            } // if
        } // try
        catch (DBError err)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (err, this.app, this.sess, this.env, false);
            variables = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return variables;
    } // getVariablesForInstance


    /**************************************************************************
     * Stores all variables of given workflow-instance and given
     * variable-object into db. <BR/> INSERTs them if not existent and
     * UPDATES them otherwise.
     *
     * @param workflowInstance      the workflow instance
     * @param variables             the variables-object
     *
     * @return  <CODE>true</CODE> if ok
     *          <CODE>false</CODE> otherwise
     */
    protected boolean storeVariablesOfInstance (Workflow_01 workflowInstance,
                                                Variables variables)
    {
        Variable variable;

        // iterate through variables and call procedure for each of them:
        // for each of them:
        for (Iterator<Variable> iter = variables.iterator (); iter.hasNext ();)
        {
            // get next element:
            variable = iter.next ();

            // truncate variables name and value if necessary
            if (variable.name.length () > 64)
            {
                variable.name = variable.name.substring (64);
            } // if
            if (variable.value.length () > 255)
            {
                variable.value = variable.value.substring (255);
            } // if

            // store variable in DB

            // create stored procedure call:
            StoredProcedure sp = new StoredProcedure ("p_WorkflowVariables$crtEntry",
                StoredProcedureConstants.RETURN_NOTHING);

            // parameter definitions:
            // must be in right sequence (like SQL stored procedure def.)

            // input parameters:
            // instance id
            BOHelpers.addInParameter (sp, workflowInstance.oid);
            // variable name
            sp.addInParameter (ParameterConstants.TYPE_STRING, variable.name);
            // variable value
            sp.addInParameter (ParameterConstants.TYPE_STRING, variable.value);

            // perform the function call:
            BOHelpers.performCallProcedureData (sp, this.env);
        } // for iter

        // exit
        return true;
    } // storeVariablesOfInstance


    /**************************************************************************
     * Deletes all variables stored for given instance. <BR/>
     *
     * @param workflowInstance      the workflow instance
     *
     * @return  <CODE>true</CODE> if ok
     *          <CODE>false</CODE> otherwise
     */
    protected boolean deleteVariablesOfInstance (Workflow_01 workflowInstance)
    {
        SQLAction action = null;        // the action object used to access the
                                        // database

        // select variables for given instance
        String queryStr = " DELETE ibs_WorkflowVariables " +
                          " WHERE instanceId = " + workflowInstance.oid.toStringQu ();

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            action.execute (queryStr, true);
        } // try
        catch (DBError err)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (err, this.app, this.sess, this.env, false);
            // exit
            return false;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // exit
        return true;
    } // deleteVariablesForInstance

} // WorkflowObjectHandler

/*
 * Class: WorkflowRightsHandler.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.ObjectNotAffectedException;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.service.workflow.RightsList;
import ibs.service.workflow.RightsListElement;
import ibs.service.workflow.RightsMapper;
import ibs.service.workflow.WorkflowConstants;
import ibs.tech.sql.DBError;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.GeneralException;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;
import ibs.util.UtilExceptions;

import java.util.Iterator;


/******************************************************************************
 * Holds methods to handle objects and rights changed during a workflow. <BR/>
 *
 * @version     $Id: WorkflowRightsHandler.java,v 1.17 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowRightsHandler extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowRightsHandler.java,v 1.17 2010/04/07 13:37:10 rburgermann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an WorkflowDefinition Object. <BR/>
     */
    public WorkflowRightsHandler ()
    {
        // nothing to do
    } // constructor


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////


// Methods:
// - changeOwner
// - getRights -> RightsList (holds all rights for every group/user)
// - setRights(RightsList) -> set given rights for object
// --> important: only rights-difference should be set!
// --> recursive must be possible

// RightsList
// - addRights
// - removeRights
// - changeRights

// Problem:
// - Gruppen/Benutzer-Zuordnungen
// - Im Workflow gibt es nur mehr Einzelbenutzer (Keine Gruppenrechte)




    /**************************************************************************
     * Get list of rights for given BusinessObject. <BR/>
     *
     * @param   objectId    id of object
     *
     * @return  RightsList  list of rights for given object
     *          <CODE>null</CODE> if an error occured
     */
    public RightsList getRights (OID objectId)
    {
        this.debug ("WorkflowRightsHandler.getRights START: objectId = " + objectId.toString ());

        RightsList list = null;         // return value
        int rowCount = 0;               // row counter
        SQLAction action = null;        // the action object used to access the
                                        // database

        // retrieves all rights for the given object
        String queryStr = "SELECT r.rPersonId, r.rights" +
                          " FROM   ibs_Object o, ibs_RightsKey rk," +
                          " ibs_RightsKeys r" +
                          " WHERE  o.oid = " + objectId.toStringQu () +
                          " AND o.rkey = rk.id" +
                          " AND rk.rKeysId = r.id";

        this.debug ("Query: " + queryStr);

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // execute the query
            rowCount = action.execute (queryStr, false);

            // check for empty resultset
            if (rowCount <= 0)
            {
                return null;            // error: terminate this method
                                        // every object MUST have a rights entry!!
            } // if

            // initialize rights list
            list = new RightsList ();

            // get all tuples out of db
            while (!action.getEOF ())
            {
                // retrieve data and create a new rights element
                if (!list.addEntry (action.getInt ("rPersonId"),
                                    action.getInt ("rights"),
                                    false))
                                            // error when adding
                {
                    list = null;
                    break;
                } // if

                // proceed to next tuple
                action.next ();

            } // while
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            list = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        this.debug ("List = " + list.toString ());
        this.debug ("WorkflowRightsHandler.getRights END");

        return list;
    } // getRights


    /***************************************************************************
     * Set list of rights for given BusinessObject. <BR/>
     *
     * @param   objectId    id of object
     * @param   list        list of rights for object
     * @param   user        the user with whom the operation will be
     *                      performed (e.g. the system-user)
     *
     * @return  <CODE>true</CODE>        if all rights could have been set
     *          <CODE>null</CODE>        if an error occured
     */
    public boolean setRightsList (OID objectId, RightsList list, User user)
    {
        this.debug ("WorkflowRightsHandler.setRightsList START: objectId = " +
            objectId + "; List = " + list.toString ());

        // local variables
        RightsListElement elem;

        // loop through given rights list and update changed rights only:
        for (Iterator<RightsListElement> iter = list.iterator ();
            iter.hasNext ();)
        {
            // get next element:
            elem = iter.next ();

            // check if element has been changed
            if (elem.changed)
            {
                // perform setRights call:
                // - if rights=0 the entry for this user will be deleted
                // - no recursion
                this.performSetRights (objectId,
                                       elem.rights,
                                       elem.rPersonId,
                                       false);
            } // if
        } // for iter

        // when done: propagate rights to all subobjects
        try
        {
            this.performSetRightsRecData (objectId, user);
        } // try
        catch (GeneralException e)
        {
            this.debug ("WorkflowRightsHandler.setRightsList ERROR in performSetRightsRecData " + e.getMessage ());

            // some error occured
            return false;
        } // catch

        this.debug ("WorkflowRightsHandler.setRightsList END");

        // exit method
        return true;
    } // setRightsList


    /***************************************************************************
     * DB-call: Set rights for a given user/group on a business object handled
     * by the workflow. <BR/>
     *
     * @param   objectId    Id of object to add rights.
     * @param   rights      Rights to add.
     * @param   rPersonId   Id of the user who will get the rights.
     * @param   rec         Include subobjects.
     */
    public void performSetRights (OID objectId,
                                  int rights,
                                  int rPersonId,
                                  boolean rec)
    {
        this.debug ("WorkflowRightsHandler.performSetRights START: objectId = " +
            objectId.toString () + "; rights = " + rights +
            "; rPersonId = " + rPersonId + "; rec = " + rec);

        StoredProcedure sp = new StoredProcedure ("p_Workflow$setRights",
            StoredProcedureConstants.RETURN_NOTHING);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters
        // objectId
        BOHelpers.addInParameter (sp, objectId);
        // user id
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, rPersonId);
        // rights
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, rights);
        // rec
        sp.addInParameter (ParameterConstants.TYPE_BOOLEAN, rec);

        // perform the function call:
        BOHelpers.performCallProcedureData (sp, this.env);

        this.debug ("WorkflowRightsHandler.performSetRights END");
    } // performSetRights


////////////////////////
//
// the core of this method is a copy of
// RightsContainer_01.performSetRightsRecData
//
// the method-header has been changed
//
    /**************************************************************************
     * Set the rights of all sub-objects for the given object; the operation
     * will be executed with the given user. <BR/>
     * During this operation a rights check is done, too.
     * If this is all right the object is stored and this method terminates
     * otherwise an exception is raised. <BR/>
     *
     * @param   objectId    Operation to be performed with the object.
     * @param   user        The user with whom the operation will be
     *                      performed (e.g. the system-user).
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   ObjectNotAffectedException
     *              It was not possible to set the rights of all objects.
     */
    protected void performSetRightsRecData (OID objectId, User user)
        throws NoAccessException, ObjectNotAffectedException
    {
        this.debug ("WorkflowRightsHandler.performSetRightsRecData START: objectId = " + objectId);

        int retVal = UtilConstants.QRY_OK;            // return value of query
        StoredProcedure sp = new StoredProcedure (
            "p_Rights$setRightsRecursive", StoredProcedureConstants.RETURN_VALUE);

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters
        // oid
        BOHelpers.addInParameter (sp, objectId);

        // user id - user is not important (should be SYSTEM)
        sp.addInParameter (
            ParameterConstants.TYPE_INTEGER, user != null ? user.id : 0);

        // operation - no operation necessary
        sp.addInParameter (ParameterConstants.TYPE_INTEGER, 0);

        // perform the function call:
        retVal = BOHelpers.performCallFunctionData (sp, this.env);

        // check the rights
        if (retVal == UtilConstants.QRY_INSUFFICIENTRIGHTS)
                                        // access not allowed?
        {
            // raise no access exception
            NoAccessException error =
                new NoAccessException (MultilingualTextProvider.getMessage (
                    UtilExceptions.EXC_BUNDLE, UtilExceptions.ML_E_NOACCESSEXCEPTION, env));
            throw error;
        } // if access not allowed
        else if (retVal == UtilConstants.QRY_NOTALLAFFECTED)
        {
            // raise object(s) not affected exception
            ObjectNotAffectedException error = new ObjectNotAffectedException (
                MultilingualTextProvider.getMessage (UtilExceptions.EXC_BUNDLE,                                            
                UtilExceptions.ML_E_OBJECTNOTAFFECTEDEXCEPTION, env));
            throw error;
        } // else if
        else                          // access allowed
        {
            // room for some success statements
        } // else access allowed

        this.debug ("WorkflowRightsHandler.performSetRightsRecData END");
    } // performSetRightsRecData


    /**************************************************************************
     * Set the rights-key of the object with the given oid1 (incl. sub-objects)
     * to the rightskey of the object with the given oid2. <BR/>
     *
     * @param   oid1    oid of the object for which rights-key will be changed
     *                  (incl. sub-objects)
     * @param   oid2    oid of the object of which rights-key will be copied
     *
     * @return  <CODE>true</CODE> if ok; <CODE>false</CODE> if an error occurred
     */
    protected boolean copyRightsKeyRec  (OID oid1, OID oid2)
    {
        this.debug ("WorkflowRightsHandler.copyRightsKeyRec START: oid1 = " + oid1 + "; oid2 = " + oid2);

        int retVal = UtilConstants.QRY_OK;            // return value of query
        StoredProcedure sp = new StoredProcedure (
            "p_Workflow$copyRightsRec", StoredProcedureConstants.RETURN_VALUE);

        // check arguments:
        if (oid1 == null || oid2 == null)
        {
            return false;
        } // if

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)

        // input parameters:
        // oid1
        BOHelpers.addInParameter (sp, oid1);

        // oid2
        BOHelpers.addInParameter (sp, oid2);

        // perform the function call:
        try
        {
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            // can not occur
        } // catch

        this.debug ("WorkflowRightsHandler.copyRightsKeyRec END");

        // check success
        if (retVal != UtilConstants.QRY_OK)
        {
            return false;
        } // if

        return true;
    } // copyRightsKeyRec


    /***************************************************************************
     * Get object for mapping of m2 rights to workflow rights. <BR/>
     *
     * @return  WorkflowRightsMapper    holds rights mapping
     *          <CODE>null</CODE>       if an error occured
     */
    public RightsMapper getRightsMapping ()
    {
        this.debug ("WorkflowRightsHandler.getRightsMapping START");
        RightsMapper mapping = null;         // return value
        int rowCount = 0;               // row counter
        SQLAction action = null;        // the action object used to access the
                                        // database

        // retrieves all rights for the given object
        String queryStr = " SELECT  m.aliasName as name, SUM(o.id) as rights " +
                          " FROM    ibs_RightsMapping m, ibs_Operation o " +
                          " WHERE   UPPER(m.rightName) = UPPER(o.name) " +
                          " GROUP   by m.aliasName";

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();

            // execute the query
            rowCount = action.execute (queryStr, false);

            // check for empty resultset
            if (rowCount <= 0)
            {
                return null;            // error: terminate this method
                                        // there must be some entries!!
            } // if

            // initialize rights list
            mapping = new RightsMapper ();

            // get all tuples out of db
            while (!action.getEOF ())
            {
                // retrieve data and create a new rights element
                if (!mapping.addEntry (action.getString ("name"),
                    action.getInt ("rights")))
                {
                    // error while adding
                    mapping = null;
                    break;
                } // if

                // proceed to next tuple
                action.next ();

            } // while
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
            mapping = null;
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        // add additional entry to mapping:
        // NONE=0: it has no entry in the db
        if (mapping != null)
        {
            mapping.addEntry (WorkflowConstants.RIGHTS_ALIAS_NONE, 0);
        } // if

        this.debug ("Mapping = " + mapping.toString ());
        this.debug ("WorkflowRightsHandler.getRightsMapping END");

        return mapping;
    } // getRightsMapping

} // WorkflowRightsHandler

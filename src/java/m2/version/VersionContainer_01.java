/*
 * VersionContainer_01.java
 */

// package:
package m2.version;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.bo.type.Type;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DataElement;
import ibs.di.DocumentTemplate_01;
import ibs.di.ValueDataElement;
import ibs.di.XMLContainer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;

import m2.version.Version_01;

import java.util.Vector;


/******************************************************************************
 * This class realizes the implementation of a version container which is
 * allowed to contain only version objects.
 *
 * @version     $Id: VersionContainer_01.java,v 1.13 2010/04/07 13:37:14 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class VersionContainer_01 extends XMLContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: VersionContainer_01.java,v 1.13 2010/04/07 13:37:14 rburgermann Exp $";


    /**
     * The typecode for this container. <BR/>
     */
    private static final String TYPECODE = "m2VersionContainer";

    /**
     * The classname of the elements which are stored in the container. <BR/>
     */
    private static final String ELEMENTCLASSNAME = "m2.version.VersionContainerElement_01";


    /**************************************************************************
     * This constructor creates a new instance of the class XMLContainer. <BR/>
     */
    public VersionContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
        this.displayTabs = true;
    } // VersionContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class VersionContainer_01. <BR/>
     * The compound object id is used as base for getting the
     * <A HREF="#server">server</A>, <A HREF="#type">type</A>, and
     * <A HREF="#id">id</A> of the business object. These values are stored in the
     * special public attributes of this type. <BR/>
     * The <A HREF="#user">user object</A> is also stored in a specific attribute
     * of this object to make sure that the user's context can be used for getting
     * his/her rights.
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public VersionContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
        this.displayTabs = true;
    } // VersionContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();
        this.displayTabs = true;

        // set name of specific container element
        this.elementClassName = this.getElementClassname ();
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        int[] newButtons = new int[2];
/* KR 20050930 don't display checkout and checkin buttons within version
 *             and version container
        Vector checkOutInfo = null;
        OID checkedOutOid = null;
        int userCheckOutId = -1;

        try
        {
            checkOutInfo = getCheckedOutInfo ();
        } // try
        catch (NoAccessException e)
        {
            checkOutInfo = null;
        } // catch

        if (checkOutInfo != null)
        {
            checkedOutOid = (OID) checkOutInfo.elementAt (0);
            userCheckOutId = ((Integer) checkOutInfo.elementAt (1)).intValue ();
        } // if

        // check if checkedOutObjects exist, if so then insert the
        // checkin button else insert the checkout button
        if (!isEmpty () && checkedOutOid != null)
        {
            // show button only if user is allowed to check in the object
            if (this.oid.equals (checkedOutOid) &&
                this.user.id == userCheckOutId)
            {
                newButtons[0] = Buttons.BTN_EDITBEFORECHECKINCONTAINER;
            } // if user allowed to see the button
        } // if
        else if (!isEmpty ())
        {
            newButtons[0] = Buttons.BTN_LISTDELETE;
            newButtons[1] = Buttons.BTN_CHECKOUTCONTAINER;
        } // else
        else if (isEmpty ())
        {
            newButtons[0] = Buttons.BTN_NEW;
        } // else if
 */
        // if the version container is not empty display the "delete" button,
        // otherwise display the "new" button
        if (!this.isEmpty ())
        {
            newButtons[0] = Buttons.BTN_LISTDELETE;
        } // else
/*
        else
        {
            newButtons[0] = Buttons.BTN_NEW;
        } // else
*/

        // return button array
        return newButtons;
    } // setContentButtons


    /**************************************************************************
     * This method sets this object as a master which has the highest
     * version number if no master is set. <BR/>
     *
     * @param   table   The table name.
     */
    public void updateMasterVersion (String table)
    {
        SQLAction action = this.getDBConnection ();
        StringBuffer query = new StringBuffer ();
        OID masterOid = null;

        // create the query which looks for the new master. the query gets the
        // highest version number out of the table where the representet object
        // will be the new master
        query.append ("select v.oid from ").append (table);
        query.append (" v, ibs_object o ");
        query.append (" where o.containerId = ").append (this.oid.toStringQu ());
        query.append (" and o.oid = v.oid ");
        query.append (" and o.state = ").append (States.ST_ACTIVE);
        query.append (" and v.").append (this.getDBFieldVersion ()).append (" = ");
        query.append (" (select MAX (ver.").append (this.getDBFieldVersion ()).append (")");
        query.append ("  from ").append (table).append (" ver, ibs_object obj");
        query.append ("  where obj.containerId = ").append (this.oid.toStringQu ());
        query.append ("    and obj.oid = ver.oid ");
        query.append ("    and obj.state = ").append (States.ST_ACTIVE).append (")");

        try
        {
            // get the number of datasets:
            int rowCount = action.execute (query, false);

            // not empty result set ?
            if (rowCount > 0)
            {
                // get tuples out of db
                // add the name and the oid of the document template
                masterOid = SQLHelpers.getQuOidValue (action, "oid");
            } // if tuples found

            // end transaction
            action.end ();
////////////////////////////////////////
// HACK BM BEGIN
////////////////////////////////////////

            if (masterOid != null)
            {
                // get the version container element and the oid of the
                // actual business object version
                // retrieve the version and check if it is the master
                // or not. if it is the master return the object.
                // the cache must not be used: getparameters is called
                // there which produces a side effect!! it is setting
                // some properties with the actual object which is not
                // correct!!!!!!!!!!!!!!
//                Version_01 v = getNewAndInitVersion (masterOid);
//                v.retrieve (Operations.OP_READ);

                Version_01 v = (Version_01) this.getObjectCache ().fetchObject (
                    masterOid, this.user, this.sess, this.env, false);

                v.setMaster (true);
                v.performChange (Operations.OP_CHANGE);
            } // if (rowCount > 0)
////////////////////////////////////////
// HACK BM END
////////////////////////////////////////
        } // try
        catch (DBError error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (TypeNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectClassNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectInitializeException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INITIALIZATIONFAILED, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (NameAlreadyGivenException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_NAMEALREADYGIVEN, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        finally
        {
            // close db connection in every case:
            this.releaseDBConnection (action);
        } // finally
    } // updateMasterVersion


    /**************************************************************************
     * This method retrieves the master of the container. if there does not
     * exist one null is returned, else the object itself. <BR/>
     *
     * @return  The master is returned or null if it does not exist.
     */
    public Version_01 getMasterVersion ()
    {
/*
        try
        {
            if (this.elements == null ||
                (this.elements != null && this.elements.size () == 0))
            {
                performRetrieveContentData (Operations.OP_READ, 0,
                                            BOConstants.ORD_FULLNAME);
            } // if no elements in list

            // if elements in the container
            if (this.elements != null && this.elements.size () > 0)
            {
                VersionContainerElement_01 vContElem =
                    (VersionContainerElement_01) this.elements.elementAt (0);

                // to get the table name from the form there is an element
                // of the container needed
                Type t = this.app.cache.getType (vContElem.oid.tVersionId);
                String table = t.getTemplate ().getMappingTableName ();
                return getMasterVersion (table);
            } // if elements found
        } // try
        catch (NoAccessException e)
        {
            showNoAccessMessage (Operations.OP_READ);
        } // catch
        return null;
*/
        // get the typeId of the Version_01 object type used with this
        // version container. The user can extend the Version_01 type
        // by defining a new XML template. This user defined XML type
        // uses another database table tgo store the object attributes.
        // we need the name of this specific table to determinate the master.
        String[] typeIds = this.getTypeIds ();
        if (typeIds != null && typeIds.length > 0)
        {
            // the version container must contain exactly one object type
            Type t = this.getTypeCache ().getType (Integer.parseInt (typeIds[0]));
            if (t != null)
            {
                // return the master version object
                return this.getMasterVersion (((DocumentTemplate_01) t
                    .getTemplate ()).getMappingTableName ());
            } // if the type is valid
        } // if id table is valid

        return null;
    } // getMasterVersion


    /**************************************************************************
     * This method retrieves the master of the container. if there does not
     * exist one <CODE>null</CODE> is returned, else the object itself. <BR/>
     *
     * @param   table   The table name.
     *
     * @return  The master is returned or null if it does not exist.
     */
    public Version_01 getMasterVersion (String table)
    {
        SQLAction action = this.getDBConnection ();
        StringBuffer query = new StringBuffer ();
        OID masterOid = null;

        // create the query
        query.append ("select v.oid from ").append (table);
        query.append (" v, ibs_object o ");
        query.append (" where o.containerId = ").append (this.oid);
        query.append (" and o.oid = v.oid ");
        query.append (" and v.").append (this.getDBFieldMaster ()).append (" = 1");
        query.append (" and state = ").append (States.ST_ACTIVE);

        try
        {
            // count the result rows of the query
            int rowCount = action.execute (query.toString (), false);

            // not empty result set ?
            if (rowCount > 0)
            {
                // get tuple out of db
                // the the oid
                masterOid = new OID (action.getString ("oid"));
            } // if tuples found

            // end transaction
            action.end ();

            if (masterOid != null)
            {
                // get the version container element and the oid of the
                // actual business object version
                // retrieve the version and check if it is the master
                // or not. if it is the master return the object.
                // the cache must not be used: getparameters is called
                // there which produces a side effect!! it is setting
                // some properties with the actual object which is not
                // correct!!!!!!!!!!!!!!
//                Version_01 v = getNewAndInitVersion (masterOid);
//                v.retrieve (Operations.OP_READ);

                Version_01 v = (Version_01) this.getObjectCache ().fetchObject (
                    masterOid, this.user, this.sess, this.env, false);

                // if the object is the master version then return it
                if (v.isMasterVersion ())
                {
                    // the masterversion is returned
                    return v;
                } // if it is the masterversion
            } // if (rowCount > 0)
        } // try
        catch (DBError error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (IncorrectOidException e)
        {
            this.showIncorrectOidMessage (masterOid.toString ());
        } // catch
        catch (ObjectNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (TypeNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectClassNotFoundException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectInitializeException error)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INITIALIZATIONFAILED, this.env),
                    this.app, this.sess, this.env);
        } // catch

        // if no master found return null
        return null;
    } // getMasterVersion


    /**************************************************************************
     * This method returns the state information if the container is empty or
     * not.
     *
     * @return  <CODE>true</CODE> if the container is empty,
     *          <CODE>false</CODE> if at least one element is in it.
     */
    public boolean isEmpty ()
    {
        return this.elements == null || this.elements.size () == 0;
    } // isEmpty


    /**************************************************************************
     * Overrides the base method and do the checkout. The container looks first
     * for the master version and checks this object out. <BR/>
     *
     * @return  The version which was master and checked out is returned or
     *          <CODE>null</CODE> if the master version was not found.
     *
     * @throws  NoAccessException
     *          If the access is forbidden the exception is thrown.
     */
    public BusinessObject checkOut () throws NoAccessException
    {
        Version_01 master = this.getMasterVersion ();

        // if the container has a version and the version was found then
        // get the master of the version
        if (master != null)
        {
            // get the master of the container
            master = this.getMasterVersion ();

            // check the master out
            master.checkOut ();
        } // if not null

        return master;
    } // checkOut


    /**************************************************************************
     * Overrides the base method and do some further stuff. <BR/>
     *
     * @return  The version container is returned. This is needed to know
     *          what to show next.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    public BusinessObject checkIn () throws NoAccessException
    {
        Version_01 master = this.getMasterVersion ();

        // if the container has a version and the version was found then
        // get the master of the version
        if (master != null)
        {
            // get the master of the container
            master = this.getMasterVersion ();

            // check the master in
            master.checkIn ();
        } // if not null

        return this;
    } // checkIn


    /**************************************************************************
     * This method just realizes the retrieving of an object with a specific
     * oid from the cache. <BR/>
     *
     * @param   oid     The oid of the object to get from the cache.
     * @return  The version is returned which is represented by the oid or
     *          <CODE>null</CODE> if the object does not exist.
     */
    private Version_01 fetchObjectFromCache (OID oid)
    {
        try
        {
            // fetch the object from the cache and return it
            return (Version_01) this.getObjectCache ().fetchObject
                (oid, this.user, this.sess, this.env, true);
        } // try
        catch (ObjectNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_OBJECTNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (TypeNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectInitializeException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INITIALIZATIONFAILED, this.env),
                    this.app, this.sess, this.env);
        } // catch

        return null;
    } // fetchObjectFromCache


    /**************************************************************************
     * This method selects the object from the container which is possible
     * to be checked in. If no object is found which is checked out null is
     * returned. <BR/>
     *
     * @return  The object which can be checked in.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    protected BusinessObject getObjectForCheckIn () throws NoAccessException
    {
        return this.getMasterVersion ();
    } // getObjectForCheckIn


   /***************************************************************************
     * This method selects the object from the container which is possible
     * to be checked out. The object which is possible to be checked out is
     * the master. If no object is found which is checked out null is
     * returned. <BR/>
     *
     * @return  The object which can be checked out.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    protected BusinessObject getObjectForCheckOut () throws NoAccessException
    {
        return this.getMasterVersion ();
    } // getObjectForCheckIn


    /**************************************************************************
     * Selects the object out of the container which should be checked out next.
     * <BR/>
     *
     * @return  The object which should be checked out or
     *          <CODE>null</CODE> if none is available.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    protected BusinessObject getCheckedOutObject () throws NoAccessException
    {
        // get the oid from the object which shall be checked out
        Vector<Object> checkOutInfo = this.getCheckedOutInfo ();

        // if an oid is found for checking out the object will be retrieved
        // via the cache and returned.
        if (checkOutInfo != null)
        {
            OID oidCheckedOut = (OID) checkOutInfo.elementAt (0);
            return this.fetchObjectFromCache (oidCheckedOut);
        } // if an element is checked out

        return null;
    } // getCheckedOutObject


    /**************************************************************************
     * The method is used to find out the field in the db for the version.
     *
     * @return  The string representing the column name of the db for the
     *          version.
     */
    protected String getDBFieldVersion ()
    {
        return Version_01.DBFIELD_VERSION;
    } // getDBFieldVersion


    /**************************************************************************
     * The method is used to find out the field in the db for isMaster.
     *
     * @return  The string representing the column name of the db for isMaster.
     */
    protected String getDBFieldMaster ()
    {
        return Version_01.DBFIELD_MASTER;
    } // getDBFieldVersion


    /**************************************************************************
     * The method is used to find out the typecode for this container.
     *
     * @return  The string representing the typecode for this container.
     */
    public static String getTypecode ()
    {
        return VersionContainer_01.TYPECODE;
    } // getTypecode


    /**************************************************************************
     * Return the classname for the objects in the container. <BR/>
     *
     * @return  The classname of the objects in the container.
     */
    public String getElementClassname ()
    {
        return VersionContainer_01.ELEMENTCLASSNAME;
    } // getElementClassname


    /**************************************************************************
     * This method finds the info for the first object found which is
     * checked out in the container. <BR/>
     *
     * @return  A vector where the first element contains the OID of the object
     *          checked out and the second the userId of the user who checked
     *          the object out. If no object checked out null is returned.
     *
     * @throws  NoAccessException
     *          An access error occurred.
     */
    public Vector<Object> getCheckedOutInfo () throws NoAccessException
    {
        SQLAction action = this.getDBConnection ();
        StringBuffer query = new StringBuffer ();
        OID checkedOutOid = null;
        int userId = -1;

        // create the query. the query should find the checked out object
        // from that container. it can be one at a time only because only the
        // master can be checked out
        query.append ("select c.oid, c.userId from ibs_checkout_01 c where c.oid in ");
        query.append (" (select oid from ibs_object where containerid = ");
        query.append (this.oid.toString ()).append (" and state = ");
        query.append (States.ST_ACTIVE).append (")");

        int rowCount = 0;
        try
        {
            // get the number of result sets
            rowCount = action.execute (query, false);
            // check if a data set was found
            if (rowCount > 0)
            {
                // get tuples out of db
                // add the name and the oid of the document template
                checkedOutOid = new OID (action.getString ("oid"));
                userId = action.getInt ("userId");
            } // if

            action.end ();
        } // try
        catch (DBError e)
        {
            // display error message:
            IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
        } // catch
        catch (IncorrectOidException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INCORRECTOID, this.env),
                    this.app, this.sess, this.env);
        } // catch
        finally
        {
            try
            {
                action.end ();
            } // try
            catch (DBError e)
            {
                // should not occur, just display error message:
                IOHelpers.showMessage (e, this.app, this.sess, this.env, true);
            } // catch
        } // finally

        Vector<Object> v = new Vector<Object> ();
        v.addElement (checkedOutOid);
        v.addElement (new Integer (userId));
        return v;
    } // getCheckedOutInfo


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for Databaseoperation.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        // initialize used properties
        this.useStandardHeader = true;
        this.headerFields = "";
        this.headerFieldsArray = null;
        this.childValues = new Vector<ValueDataElement> ();

        // get document template for this container
        DocumentTemplate_01 template = (DocumentTemplate_01) this.typeObj.getTemplate ();
        DataElement dataElement = template.getTemplateDataElement ();

        // set mayContain
        this.mayContain = template.getMayContain ();

        DataElement mayContainDataElement = null;    // de for maycontain-type

        // if there is more than one maycontain - type, the content
        // will be retrieved from xml-files

        // set flag
        this.getContentFromDB = false;

        // get definition of which fields should be shown in content
        this.setFieldsAndAttributes (dataElement, mayContainDataElement, action);
    } // performRetrieveSpecificData

} // VersionContainer_01

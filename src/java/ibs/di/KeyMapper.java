/*
 * Class: KeyMapper.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.IncorrectOidException;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.io.Environment;
//KR TODO: unsauber
import ibs.io.IOHelpers;
//KR TODO: unsauber
import ibs.io.session.ApplicationInfo;
//KR TODO: unsauber
import ibs.io.session.SessionInfo;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;
import ibs.tech.sql.StoredProcedureConstants;
import ibs.util.NoAccessException;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents one object of type KeyMapper. <BR/>
 * KeyMapper Objects represent the link between an system object id and an
 * external id. The foreign key is composed by the external id and an id domain
 * descriptor. <BR/>
 *
 * @version     $Id: KeyMapper.java,v 1.18 2009/09/04 18:33:54 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 990316
 ******************************************************************************
 */
public class KeyMapper extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: KeyMapper.java,v 1.18 2009/09/04 18:33:54 kreimueller Exp $";


    /**************************************************************************
     * This private class holds the information of an external key.
     **************************************************************************
     */
    public static class ExternalKey
    {
        /**
         * Domain of the id. <BR/>
         */
        protected String p_domain = null;

        /**
         * The id itself. <BR/>
         */
        protected String p_id = null;

        /**
         * The resolved oid. <BR/>
         */
        protected OID p_oid = null;


        /**********************************************************************
         * The constructor
         * @param   domain  the domain name of the ext. key
         * @param   id      the id of the ext. key
         */
        public ExternalKey (String domain, String id)
        {
            this.p_domain = domain;
            this.p_id = id;
        } // ExternalKey

        /**********************************************************************
         * The constructor
         * @param   oid     the resolved oid of the external key
         * @param   domain  the domain name of the ext. key
         * @param   id      the id of the ext. key
         */
        public ExternalKey (OID oid, String domain, String id)
        {
            this.p_oid = oid;
            this.p_domain = domain;
            this.p_id = id;
        } // ExternalKey



        // getter methods:
        /**********************************************************************
         * Get the domain of the external key. <BR/>
         *
         * @return  The domain.
         */
        public final String getDomain ()
        {
            return this.p_domain;
        } // getDomain


        /**********************************************************************
         * Get the id of the external key. <BR/>
         *
         * @return  The id.
         */
        public final String getId ()
        {
            return this.p_id;
        } // getId


        /**********************************************************************
         * Get the resolved oid of the external key. <BR/>
         *
         * @return Returns the p_oid.
         */
        public final OID getOid ()
        {
            return this.p_oid;
        } // getOid


        /**********************************************************************
         * Set the resolved oid of the external key. <BR/>
         *
         * @param oid The p_oid to set.
         */
        public final void setOid (OID oid)
        {
            this.p_oid = oid;
        } // setOid


        /**********************************************************************
         * Check if the ID/IDDOMAIN pair is an internal key. <BR/>
         * This is indicated by IDOMAIN=domain and ID=oid. <BR/>
         * In that case we assume that the object originated from the same
         * system. <BR/>
         * Note that this methods set the oid then the check was successful
         * and the oid can be read using #getOid()
         *
         * @param   domain  The domain to be checked.
         *
         * @return  true in case it was a valid internal key or false otherwise
         *
         * @see #getOid()
         */
        public final boolean isInternalKey (String domain)
        {
            // if the domain is the system domain and
            // the id is a valid OID we use this as the OID
            if (this.p_domain.equals (domain))
            {
                try
                {
                    this.setOid (new OID (this.p_id));
                    return true;
                } // try
                catch (IncorrectOidException e)
                {
                    return false;
                } // catch
            } // if (this.p_domain.equals (domain))
            return false;
        } // isInternalKey

    } // class ExternalKey


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor creates a new instance of the class KeyMapper. <BR/>
     *
     * @param   user    the current user
     * @param   env     the current environment
     * @param   sess    the current session
     * @param   app     the current application info
     */
    public KeyMapper (User user, Environment env, SessionInfo sess,
        ApplicationInfo app)
    {
        // call constructor of super class
        super ();

        this.initObject (OID.getEmptyOid (), user, env, sess, app);
    } // KeyMapper


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
    } // initClassSpecifics


    /**************************************************************************
     * Returns the domain name for the current m2 system
     *
     * @return  the domain name of the current m2 system
     */
    public final String getDomain ()
    {
        return this.app.p_system.getSystemDomainName ();
    } // getDomain


    /**************************************************************************
     * Created an keyMapper object in the database. <BR/>
     *
     * @param   extKey      the external key for the BusinessObject
     *
     * @return true on success
     */
    public boolean performCreateMapping (ExternalKey extKey)
    {
        if (extKey == null)
        {
            return false;
        } // if

        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_KeyMapper$new",
            StoredProcedureConstants.RETURN_VALUE);
        int retVal = UtilConstants.QRY_OK;          // return value of query

        // parameter definitions:
        // must be in right sequence (like SQL stored procedure def.)
        // objectOid
        BOHelpers.addInParameter (sp, extKey.getOid ());
        // id
        sp.addInParameter (ParameterConstants.TYPE_STRING, extKey.p_id);
        // domain
        sp.addInParameter (ParameterConstants.TYPE_STRING, extKey.p_domain);

        try
        {
            // perform the function call:
            retVal = BOHelpers.performCallFunctionData (sp, this.env);
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, this.env, true);
            return false;
        } // catch

        // return true if the keymapping was created successfully
        return retVal == UtilConstants.QRY_OK;
    } // performCreateMapping


    /**************************************************************************
     * Get the OID out of the database for the given external key. <BR/>
     *
     * @param   extKey      the domain/id
     * @return  the OID for the ext. key or <CODE>null</CODE> if not found
     */
    public OID performResolveMapping (ExternalKey extKey)
    {
        if (extKey == null)
        {
            return null;
        } // if parameter invalid

        // check if the key is an internal key
        // that consists of IDOMAIN=domain ad ID=oid
        if (extKey.isInternalKey (this.getDomain ()))
        {
            return extKey.getOid ();
        } // if (extKey.isInternalKey ())

        // resolve true external key
        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_KeyMapper$getOid",
            StoredProcedureConstants.RETURN_VALUE);

        // input parameters
        // id
        sp.addInParameter (ParameterConstants.TYPE_STRING, extKey.p_id);
        // domain
        sp.addInParameter (ParameterConstants.TYPE_STRING, extKey.p_domain);
        // output parameters
        // oid
        Parameter paramOid = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);

        try
        {
            // perform the function call:
            int retVal = BOHelpers.performCallFunctionData (sp, this.env);
            if (retVal == UtilConstants.QRY_OK)
            {
                extKey.setOid (SQLHelpers.getSpOidParam (paramOid));
                return extKey.getOid ();
            } // if found
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, this.env, true);
        } // catch
        return null;
    } // performResolveMapping


    /**************************************************************************
     * Get the DOMAIN/ID pair (EXTKEY) out of the database for the given OID. <BR/>
     * If no EXTKEY is defined and the parameter 'createDefault' is true,
     * the default EXTKEY is created and returned.
     *
     * @param   objectOid       the oid of the BusinessObject
     * @param   createDefault   should be created a default EXTKEY if not found
     *
     * @return  an ExternalKey object containing the domain/id pair
     *          for the object or <CODE>null</CODE> if not found
     */
    public ExternalKey performResolveMapping (OID objectOid, boolean createDefault)
    {
        if (objectOid == null)
        {
            return null;
        } // if oid is null

        // create stored procedure call:
        StoredProcedure sp = new StoredProcedure ("p_KeyMapper$getDomainID",
            StoredProcedureConstants.RETURN_VALUE);
        int retVal = UtilConstants.QRY_OK;      // return value of query

        // input parameters: objectOid
        BOHelpers.addInParameter (sp, objectOid);
        // output parameter: id
        Parameter paramId = sp.addOutParameter (ParameterConstants.TYPE_STRING);
        // output parameter: domain
        Parameter paramDomain = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        try
        {
            ExternalKey extKey = null;

            // perform the function call:
            retVal = BOHelpers.performCallFunctionData (sp, this.env);

            if (retVal == UtilConstants.QRY_OK)
            {
                // get the found DOMAIN/ID
                extKey = new ExternalKey (objectOid,
                    paramDomain.getValueString (),
                    paramId.getValueString ());
            } // if a EXTKEY was found
            else
            {
                // if no EXTKEY is found for the OID check if the default EXTKEY
                // should be created
                if (createDefault)
                {
                    // create the default extkey: the OID and the system domain
                    extKey = new ExternalKey (
                        objectOid, this.getDomain (), objectOid.toString ());
                    if (!this.performCreateMapping (extKey))
                    {
                        // return null on error
                        return null;
                    } // if
                } // no keymapper found
            } // else if not found

            // return the extKey (or null) for the given OID
            return extKey;
        } // try
        catch (NoAccessException e)
        {
            IOHelpers.showMessage (e, this.env, true);
            return null;
        } // catch
    } // performResolveMapping

} // class KeyMapper

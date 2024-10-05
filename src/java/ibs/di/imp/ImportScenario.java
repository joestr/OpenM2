/*
 * Class: ImportScenario.java
 */

// package:
package ibs.di.imp;

// imports:
//KR TODO: unsauber
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.di.DIConstants;
import ibs.di.DataElementList;
import ibs.di.Log_01;
import ibs.di.ObjectFactory;
import ibs.di.Response;
import ibs.service.user.User;


/******************************************************************************
 * The importScenario handles imports that can not be resolved by standard
 * import functionality.
 *
 * @version     $Id: ImportScenario.java,v 1.18 2010/12/23 13:08:24 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 991013
 ******************************************************************************
 */
public abstract class ImportScenario extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportScenario.java,v 1.18 2010/12/23 13:08:24 rburgermann Exp $";


    /**
     * the objectFactory used to create business objects
     */
    protected ObjectFactory objectFactory = null;

    /**
     * the log object to store the log entries. <BR/>
     */
    protected Log_01 log = null;

    /**
     * the response object that can be used to add multipart response entries. <BR/>
     *
     */
    protected Response p_response = null;


    /**************************************************************************
     * Creates an Import Object. <BR/>
     *
     * @param oid   oid of the object
     * @param user  user that created the object
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ImportScenario (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Integrator_01


    /**************************************************************************
     * Creates an Integrator Object. <BR/>
     */
    public ImportScenario ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // Integrator_01


    /**************************************************************************
     * Sets the log object. the log must be set in order to be able
     * to write logs.
     * If not set this will cause a null pointer exception!
     * The log must be set in the class that uses the methods of an
     * importScenario object. <BR/>
     *
     * @param log   the log object
     */
    public void setLog (Log_01 log)
    {
        this.log = log;
    } // setLog


    /**************************************************************************
     * Set the response object. <BR/>
     *
     * @param   response    The response object to be set.
     */
    public void setResponse (Response response)
    {
        this.p_response = response;
    } // setResponse


    /**************************************************************************
     * Set an objectFactory object. <BR/>
     *
     * @param objectFactory   the objectFactory object
     */
    public void setObjectFactory (ObjectFactory objectFactory)
    {
        this.objectFactory = objectFactory;
    } // setObjectFactory


    /**************************************************************************
     * Get the objectFactory. In case it is not set create a new one and
     * initialize it. <BR/>
     *
     * @return an ObjectFactory object
     */
    protected ObjectFactory getObjectFactory ()
    {
        if (this.objectFactory != null)
        {
            this.objectFactory = new ObjectFactory ();
            this.objectFactory.initObject (OID.getEmptyOid (),
                    this.user, this.env, this.sess, this.app);
            this.objectFactory.setLog (this.log);
            return this.objectFactory;
        } // if (this.objectFactory != null)

        // already set
        return this.objectFactory;
    } // getObjectFactory


    /**************************************************************************
     * Shows a status bar. <BR/>
     * HINT: this is used to assure that the user does not loose his connection
     * while importing a large amount of objects. <BR/>
     */
    protected void showStatusBar ()
    {
        if (!this.log.isDisplayLog)
        {
            this.env.write (DIConstants.CHAR_STATUSBAR);
        } // if (isPrintStatusbar)
    } // showStatusBar


    /**************************************************************************
     * Process the importscript and with the data. <BR/>
     *
     * @param dataElementList       the dataElementList object containing the data
     * @param importScript          the importScript that holds control data
     * @param importContainerOid    the OID of the container the import has
     *                              been started from
     * @param importOperation       operation for the import
     *
     * @return true if processing was successfull or false otherwise
     */
    public abstract boolean process (DataElementList dataElementList,
                              ImportScript_01 importScript,
                              OID importContainerOid,
                              String importOperation);


    /***************************************************************************
     * This method is called at the start of the import. <BR/>
     * Overwrite this method in the corresponding subclasses to perform an
     * action. <BR/>
     */
    public abstract void executionOnStart ();


    /***************************************************************************
     * This method is called at the end of the import. <BR/>
     * Overwrite this method in the corresponding subclasses to perform an
     * action. <BR/>
     */
    public abstract void executionOnEnd ();

} // class ImportScenario

/*
 * Class: System.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 3, 2001
 * Time: 4:12:44 PM
 */

// package:
package ibs.app.system;

// imports:
import ibs.app.AppConstants;
import ibs.bo.BOPathConstants;
import ibs.util.file.FileHelpers;
import ibs.util.list.ElementContainer;
import ibs.util.list.IElement;
import ibs.util.list.ListException;

import java.io.File;


/*******************************************************************************
 * This class contains a list of system values. <BR/>
 *
 * @version     $Id: System.java,v 1.12 2007/07/17 12:15:36 kreimueller Exp $
 *
 * @author      kreimueller, 011122
 *******************************************************************************
 */
public class System extends ElementContainer<SystemValue>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: System.java,v 1.12 2007/07/17 12:15:36 kreimueller Exp $";


    /**
     * Absolute base path of m2 on web server. <BR/>
     * This path is used for finding files within the file system of the
     * application server.
     */
    public String p_m2AbsBasePath = null;

    /**
     * Absolute base path of m2 on web server. <BR/>
     * This path is used for finding files within the file system of the
     * application server.
     */
    public File p_m2AbsBaseDir = null;

    /**
     * Absolute webpath of m2 on web server. <BR/>
     * This path is sed for finding files on the application server.
     */
    public String p_m2WwwBasePath = null;

    /**
     * The directory containing the configuration files. <BR/>
     */
    public String p_configPath = null;

    /**
     * The directory containing the configuration files. <BR/>
     */
    public File p_configDir = null;

    /**
     * The customer name.
     */
    public String p_customerName = null;

    /**
     * The name of the system.
     */
    public String p_systemName = null;

    /**
     * The world wide unique name of this m2 system.
     * This domain name is used in EXTKEYs on import and export and is set
     * The value must be set on system installation and is stored in the
     * ibs_system table.
     * This attribute holds the value stored in the ibs_system table.
     */
    public String p_systemDomainName = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a System object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     * {@link #p_m2AbsBasePath p_m2AbsBasePath} is initialized to
     * <CODE>""</CODE>. <BR/>
     * {@link #p_m2WwwBasePath p_m2WwwBasePath} is initialized to
     * <CODE>""</CODE>. <BR/>
     *
     * @param   absBasePath The base path of the application.
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public System (String absBasePath)
        throws ListException
    {
        // call constructor of super class:
        super ();

        // set the instance's properties:
        // initialize the other instance properties:
        this.p_m2AbsBasePath = "";
        this.p_m2WwwBasePath = "";
        this.p_customerName = "";
        this.p_systemName = "";
        this.p_systemDomainName = "";

        this.setPaths (absBasePath);
    } // System


    ///////////////////////////////////////////////////////////////////////////
    // methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Set the paths. <BR/>
     * The paths are computed out of the actual class directory.
     *
     * @param   absBasePath The base path of the application.
     *
     * @throws  ListException
     *          An error occurred during trying to set the paths.
     */
    private void setPaths (String absBasePath)
        throws ListException
    {
        String absBasePathLocal = absBasePath; // variable for local assignments
        File baseDir = null;            // the base directory

        // ensure that the base directory is valid:
        absBasePathLocal = FileHelpers.makeFileNameValid (absBasePathLocal);

        // convert the url to a file:
        baseDir = new File (absBasePathLocal);

        // check if the directory was found:
        if (baseDir != null)
        {
            // set the values:
            this.p_m2AbsBaseDir = baseDir;
            this.p_m2AbsBasePath = baseDir.getPath () + File.separator;
            this.p_configPath = FileHelpers.makeFileNameValid (
                this.p_m2AbsBasePath + File.separator +
                BOPathConstants.PATH_CONF + File.separator);
            this.p_configDir = new File (this.p_configPath);
        } // if
        else
        {
            throw new ListException (
                "System: base directory not found");
        } // else
    } // setPaths


    /**************************************************************************
     * Set the domain name for the current m2 system.
     * This method is called on system startup.
     *
     * @param   domainName  the domain name of the current m2 system
     */
    public final void setSystemDomainName (String domainName)
    {
        this.p_systemDomainName = domainName;
    } // setSystemDomainName


    /**************************************************************************
     * Returns the the domain name for the current m2 system.
     *
     * @return  the domain name for the m2 system.
     */
    public final String getSystemDomainName ()
    {
        return this.p_systemDomainName;
    } // getSystemDomainName


    /**************************************************************************
     * Initialize the element class. <BR/>
     * This method shall be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see ibs.util.list.ElementContainer#setElementClass (Class)
     */
    protected void initElementClass ()
        throws ListException
    {
        this.setElementClass (SystemValue.class);
    } // initElementClass


    /**************************************************************************
     * Add a new element defined through its properties to the container. <BR/>
     *
     * @param   id      The id of the element.
     * @param   name    The name of the element.
     * @param   type    The type of the element.
     * @param   value   The value itself.
     */
    public final void add (int id, String name, String type, String value)
    {
        // create the new element:
        SystemValue elem = new SystemValue (id, name, type, value);

        // add the element to the container:
        this.add (elem);
    } // add


    /**************************************************************************
     * Add a new element to the container. <BR/>
     *
     * @param   elem    The element to be added.
     *
     * @return  <CODE>true</CODE> if the element was successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean add (SystemValue elem)
    {
        // call method of super class:
        boolean retVal = super.add (elem);

        if (retVal)                     // object was added?
        {
            // check if there shall be a property set:
/*
            if (elem.getName ().equals ("ABS_BASE_PATH"))
            {
                // set the corresponding property and ensure that there are no
                // duplicate slashes:
                this.p_m2AbsBasePath =
                    Helpers.replace (((SystemValue) elem).getValue (), "\\\\", "\\");
            } // if
*/
            if (elem.getName ().equals ("WWW_BASE_PATH"))
            {
                this.p_m2WwwBasePath = elem.getValue ();
            } // else if
            else if (elem.getName ().equals (AppConstants.CUSTOMER_NAME))
            {
                this.p_customerName = elem.getValue ();
                this.computeSystemDomainName ();
            } // else if
            else if (elem.getName ().equals (AppConstants.SYSTEM_NAME))
            {
                this.p_systemName = elem.getValue ();
                this.computeSystemDomainName ();
            } // else if
        } // if object was added

        // return the sesult:
        return retVal;
    } // add


    /**************************************************************************
     * Compute the system domain name. <BR/>
     */
    protected void computeSystemDomainName ()
    {
        if (this.p_customerName != null && this.p_systemName != null)
        {
// TODO: BB 20060323: should "m2" be replaced by openm2?
            this.p_systemDomainName =
                "m2" + this.p_customerName + "_" + this.p_systemName;
        } // if both values found
        else
        {
// TODO throw error!
//            IOHelpers.showMessage (AppMessages.MSG_NOSYSTEMDOMAINGIVEN,
//                this.app, this.sess, this.env);
            // if the values are not found in the ibs_system table
            // set the default domain name for m2 systems
            this.p_systemDomainName = "m2-0";
        } // if not found
    } // getDummy


    /**************************************************************************
     * Create a dummy element out of the id. <BR/>
     *
     * @param   id      The id from which to create the dummy object.
     *
     * @return  The dummy object.
     */
    protected IElement getDummy (int id)
    {
        // create the system value and return it.
        return new SystemValue (id);
    } // getDummy


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The system values are concatenated to create a string
     * representation according to "System superString". superString is the
     * result of <CODE>super.toString ()</CODE>.
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return "System " + super.toString ();
    } // toString

} // class System

/*
 * Class: IModule.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.list.IXMLElement;


/******************************************************************************
 * Common interface for modules. <BR/>
 *
 * @version     $Id: IModuleReference.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 29.12.2003
 ******************************************************************************
 */
public interface IModuleReference extends IXMLElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IModuleReference.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the version. <BR/>
     *
     * @return  The version if already set or <CODE>null</CODE> if not set.
     */
    public ModuleVersion getVersion ();


    /**************************************************************************
     * Check if the actual module depends on another one. <BR/>
     *
     * @param   otherObj    The other object to be checked.
     *
     * @return  <CODE>true</CODE> if the object depends on the other object,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean depends (Module otherObj);


    /**************************************************************************
     * Check if the module is active. <BR/>
     *
     * @return  <CODE>true</CODE> if the module is active,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isActive ();


    /**************************************************************************
     * Returns the string representation of the id and the version. <BR/>
     * The id and the version name are concatenated to create a string
     * representation according to <CODE>"id_version"</CODE>. <BR/>
     * e.g.: <CODE>"ibsbase_2.4.1"</CODE>
     *
     * @return  String represention of the object.
     */
    public String getIdVersion ();

} // interface IModule

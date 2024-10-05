/*
 * Class: ModuleeferenceContainer.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;

import java.util.Iterator;


/******************************************************************************
 * Container with modules. <BR/>
 *
 * @version     $Id: ModuleReferenceContainer.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class ModuleReferenceContainer extends ElementContainer<ModuleReference>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleReferenceContainer.java,v 1.1 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ModuleReferenceContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ModuleReferenceContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
    } // ModuleReferenceContainer


    ///////////////////////////////////////////////////////////////////////////
    // instance methods
    ///////////////////////////////////////////////////////////////////////////

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
        this.setElementClass (ModuleReference.class);
    } // initElementClass


    /**************************************************************************
     * Resolve the dependencies between the container elements. <BR/>
     * If there are any dependencies between the container elements ensure
     * that these dependencies are object references instead of (textual)
     * descriptions.
     *
     * @param   elems       The container with the other elements.
     *
     * @throws  ListException
     *          There occurred an error during the operation.
     */
    public void resolveDependencies (ModuleContainer elems)
        throws ListException
    {
        // loop through all elements and resolve the dependencies for each
        // one:
        for (Iterator<ModuleReference> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // resolve the dependencies for the actual element:
            (iter.next ()).resolveDependencies (elems);
        } // for iter
    } // resolveDependencies

} // class ModuleReferenceContainer

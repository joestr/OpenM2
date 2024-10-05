/*
 * Class: ConfValueContainer.java
 */

// package:
package ibs.service.module;

// imports:
import ibs.service.module.ConfValue;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;


/******************************************************************************
 * Container with configuration variables. <BR/>
 *
 * @version     $Id: ConfValueContainer.java,v 1.4 2007/07/23 12:34:23 kreimueller Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class ConfValueContainer extends ElementContainer<ConfValue>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConfValueContainer.java,v 1.4 2007/07/23 12:34:23 kreimueller Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ConfValueContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ConfValueContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
    } // ConfValueContainer


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
        this.setElementClass (ConfValue.class);
    } // initElementClass

} // class ConfValueContainer

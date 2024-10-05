/*
 * Class: ModuleContainer.java
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
 * @version     $Id: ModuleContainer.java,v 1.13 2010/03/26 12:49:54 btatzmann Exp $
 *
 * @author      Klaus, 17.12.2003
 ******************************************************************************
 */
public class ModuleContainer extends ElementContainer<Module>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ModuleContainer.java,v 1.13 2010/03/26 12:49:54 btatzmann Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a ModuleContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public ModuleContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
    } // ModuleContainer


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
        this.setElementClass (Module.class);
    } // initElementClass


    /**************************************************************************
     * Sort the elements of the container. <BR/>
     * The sorting order is defined through the behaviour of the container. <BR/>
     * This class implements the standard ordering which is by id ascending.
     *
     * @throws  ListException
     *          There occurred an error during sorting.
     */
    public void sort ()
        throws ListException
    {
        Module actElem = null;          // the actual element
        Module otherElem = null;        // the other element
        int elemCount = this.p_elems.size (); // number of elements
        int i = 0;                      // loop counter
        int pos = 0;                    // position of the element
        int lastPos = 0;                // last position of element on which
                                        // the actual element depends

        // check if the actual element class is ModuleContainer:
        if (this.getClass ().isAssignableFrom (ModuleContainer.class))
        {
            // loop through all elements and search the last depends position
            // for each one:
            for (i = 0; i < elemCount;)
            {
                lastPos = i;
                actElem = this.p_elems.elementAt (i);

                // loop through all other elements and check if the current one
                // depends on one or more of them:
                for (pos = i + 1; pos < elemCount; pos++)
                {
                    // get the element out of the vector:
                    otherElem = this.p_elems.elementAt (pos);
                    if (actElem.depends (otherElem))
                    {
                        // remember the actual position:
                        lastPos = pos;

                        // check for cyclic dependencies:
                        if (otherElem.depends (actElem))
                                        // cyclic dependency?
                        {
                            throw new ListException (
                                "Cyclic dependency between modules:" +
                                actElem.getName () + " and " +
                                otherElem.getName ());
                        } // if cyclic dependency
                    } // if
                } // for pos

                // check if there are dependencies found:
                if (lastPos > i)        // there are dependencies?
                {
                    // insert the element after its predecessors:
                    this.p_elems.remove (i);
                    this.p_elems.add (lastPos, actElem);
                } // if there are dependencies
                else                    // no dependencies
                {
                    // goto next element:
                    i++;
                } // else no dependencies
            } // for i
        } // if
        else
        {
            // use standard sorting algorithm:
            super.sort ();
        } // else
    } // sort


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
        for (Iterator<Module> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // resolve the dependencies for the actual element:
            (iter.next ()).resolveDependencies (elems);
        } // for iter
    } // resolveDependencies


    /**************************************************************************
     * Check constraints for the elements within the container. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        // loop through all alements and check the constraints for each one:
        for (Iterator<Module> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // resolve the dependencies for the actual element:
            (iter.next ()).checkConstraints ();
        } // for iter

        // resolve the dependencies:
        this.resolveDependencies (this);
        // sort the elements:
        this.sort ();
    } // checkConstraints


    /**************************************************************************
     * Load the contents for the elements within the container. <BR/>
     * Normally the installation files (xml, sql, lang) are only reloaded if
     * on or more jar files have been changed. The runtime files (layout,
     * include, stylesheets, scripts, etc.) are only loaded if there has no jar
     * file been changed since last module loading. <BR/>
     * If <CODE>reloadAll</CODE> is set to <CODE>true</CODE> both the
     * installation and the runtime files are loaded for the modules.
     *
     * @param   allConfVars All known configuration variables.
     * @param   reloadAll   Shall all module contents be reloaded?
     * @param   loadResourceBundles
     *          Shall the resource bundles be reloaded? Possible values:
     *          <CODE>Module.LOAD_RB</CODE>
     *          <CODE>Module.LOAD_ONLY_RB</CODE>
     *          <CODE>Module.LOAD_EXCLUDE_RB</CODE>
     *
     * @return  <CODE>true</CODE> if some basic resources have changed
     *          and the system has to be restarted. (e.g. java libraries).
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ListException
     *          Error when loading the contents.
     */
    public boolean loadContent (ConfVarContainer allConfVars, boolean reloadAll, int loadResourceBundles)
        throws ListException
    {
        boolean retVal = false;         // the return value

        // defines if the resouce bundles have to be reset
        boolean resetResourceBundles =
            (loadResourceBundles == Module.LOAD_ONLY_RB || loadResourceBundles == Module.LOAD_RB);
        
        // loop through all alements and check the constraints for each one:
        for (Iterator<Module> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // resolve the dependencies for the actual element:
            if ((iter.next ()).loadContent (allConfVars, reloadAll, loadResourceBundles, resetResourceBundles))
            {
                retVal = true;
            } // if
            
            // only reset the first time
            resetResourceBundles = false;
        } // for iter

        // return the result:
        return retVal;
    } // loadContent
    
    
    /**************************************************************************
     * Load the contents for a specific module within the container. <BR/>
     * Normally the installation files (xml, sql, lang) are only reloaded if
     * on or more jar files have been changed. The runtime files (layout,
     * include, stylesheets, scripts, etc.) are only loaded if there has no jar
     * file been changed since last module loading. <BR/>
     * If <CODE>reloadAll</CODE> is set to <CODE>true</CODE> both the
     * installation and the runtime files are loaded for the modules.
     *
     * @param   allConfVars All known configuration variables.
     * @param   reloadAll   Shall all module contents be reloaded?
     * @param   moduleId    the id of the module to be loaded
     *
     * @return  <CODE>true</CODE> if some basic resources have changed
     *          and the system has to be restarted. (e.g. java libraries).
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ListException
     *          Error when loading the contents.
     */
    public boolean loadContent (ConfVarContainer allConfVars, boolean reloadAll,
                                String moduleId)
        throws ListException
    {
        boolean retVal = false;         // the return value
        Module module = null;

        // does the module exist?
        if ((module = this.get (moduleId)) != null)
        {
            // load the content
            if (module.loadContent (allConfVars, reloadAll, Module.LOAD_EXCLUDE_RB, false))
            {
                retVal = true;
            } // if (module.loadContent (allConfVars, reloadAll))
        } // if ((module = get (moduleId)) != null)

        // return the result:
        return retVal;
    } // loadContent


    /**************************************************************************
     * Load the contents for a specific module within the container. <BR/>
     * Normally the installation files (xml, sql, lang) are only reloaded if
     * on or more jar files have been changed. The runtime files (layout,
     * include, stylesheets, scripts, etc.) are only loaded if there has no jar
     * file been changed since last module loading. <BR/>
     * If <CODE>reloadAll</CODE> is set to <CODE>true</CODE> both the
     * installation and the runtime files are loaded for the modules.
     *
     * @param   allConfVars All known configuration variables.
     * @param   reloadAll   Shall all module contents be reloaded?
     * @param   module        the module to be loaded
     *
     * @return  <CODE>true</CODE> if some basic resources have changed
     *          and the system has to be restarted. (e.g. java libraries).
     *          <CODE>false</CODE> otherwise.
     *
     * @throws  ListException
     *          Error when loading the contents.
     */
    public boolean loadContent (ConfVarContainer allConfVars, boolean reloadAll,
                                Module module)
        throws ListException
    {
        boolean retVal = false;         // the return value

        // load the content
        if (module.loadContent (allConfVars, reloadAll, Module.LOAD_EXCLUDE_RB, false))
        {
            retVal = true;
        } // if (module.loadContent (allConfVars, reloadAll))

        // return the result:
        return retVal;
    } // loadContent

} // class ModuleContainer

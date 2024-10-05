/*
 * Created by IntelliJ IDEA.
 * User: Horsti
 * Date: 26.08.2002
 * Time: 17:26:38
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */

// package:
package ibs.service.workflow;

// imports:
import ibs.service.workflow.WorkflowConstants;


/******************************************************************************
 * RegisterObserverJob holds the register/unregister-information about a
 * workflow-state.
 *
 * @version     $Id: RegisterObserverJob.java,v 1.4 2007/07/24 21:27:33 kreimueller Exp $
 *
 * @author      Horsti, 26.08.2002
 ******************************************************************************
 */
public class RegisterObserverJob
{
    ///////////////////////////////////////////////
    //
    // Information that comes from the XML-definition
    //
    /**
     * ID of the observerjob. <BR/>
     */
    public int id = -1;
    /**
     * Name of the observerjob. <BR/>
     */
    public String name = WorkflowConstants.UNDEFINED;
    /**
     * Name of the observerjobs class. <BR/>
     */
    public String className = WorkflowConstants.UNDEFINED;
    /**
     * Name of the observer. <BR/>
     */
    public String observer = WorkflowConstants.UNDEFINED;


    /**************************************************************************
     * Creates ObserverJob. <BR/>
     */
    public RegisterObserverJob ()
    {
        // nothing to do
    } // RegisterObserverJob


    /**************************************************************************
     * Returns a string representation of the object. <BR/>
     *
     * @return  a string representation of the object.
     */
    public String toString ()
    {
        // declare variables
        String str = "";
        String idStr = "";
        if (this.id <= 0)
        {
            idStr = WorkflowConstants.UNDEFINED;
        } // if
        else
        {
            idStr = "" + this.id;
        } // else

        // build string
        str += "id = " + idStr + "; " +
               "name = " + this.name + "; " +
               "className = " + this.className + "; " +
               "observer = " + this.observer + "]";

        return str;
    } // toString

} // ObserverJob

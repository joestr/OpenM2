/*
 * Class: FunctionHandlerContainer.java
 */

// package:
package ibs.app.func;

// imports:
import ibs.app.AppFunctions;
import ibs.app.func.FunctionValues;
import ibs.app.func.GeneralFunctionHandler;
import ibs.tech.html.ButtonBarElement;
import ibs.util.list.ElementContainer;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The container for all function handlers. <BR/>
 *
 * @version     $Id: FunctionHandlerContainer.java,v 1.12 2011/11/21 12:44:44 gweiss Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public class FunctionHandlerContainer
    extends ElementContainer<GeneralFunctionHandler>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FunctionHandlerContainer.java,v 1.12 2011/11/21 12:44:44 gweiss Exp $";



    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a FunctionHandlerContainer object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public FunctionHandlerContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
    } // FunctionHandlerContainer


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
        this.setElementClass (GeneralFunctionHandler.class);
    } // initElementClass


    /**************************************************************************
     * Evaluate the function to be performed. <BR/>
     * This method loops through all function handlers and tries to perform the
     * function. If the function is performed once the method is teminated.
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed after this method. <BR/>
     *          {@link ibs.app.AppFunctions#FCT_NOFUNCTION AppFunctions.FCT_NOFUNCTION}
     *          if there is no function or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values)
    {
        int functionLocal = function;   // variable for local assignments
        GeneralFunctionHandler handler = null; // the found function handler

        // loop through the function handlers and try to perform the function
        // within each of them:
        for (Iterator<GeneralFunctionHandler> iter = this.iterator ();
             functionLocal != AppFunctions.FCT_NOFUNCTION && iter.hasNext ();)
        {
            // get the element out of the list:
            handler = iter.next ();

            // check if the handler should be able to handle the function:
            if (handler.getMinFunction () <= functionLocal &&
                handler.getMaxFunction () >= functionLocal)
            {
                // try to perform the function:
                functionLocal = handler.evalFunction (functionLocal, values);
                // finish the loop:
                break;
            } // if
        } // for iter

        // return which function shall be performed after this method:
        return functionLocal;
    } // evalFunction


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
        GeneralFunctionHandler actElem = null; // the actual element
        GeneralFunctionHandler compElem = null; // the element to be compared
        int elemCount = 0;              // number of elements to be sorted
        int pos = 0;                    // position of the element

        for (elemCount = this.p_elems.size (); elemCount > 0; elemCount--)
        {
            actElem = this.p_elems.elementAt (0);

            // loop through the elements and sort them:
            for (pos = 1; pos < elemCount; pos++)
            {
                // get the element out of the vector:
                compElem = this.p_elems.elementAt (pos);

                // check for overlapping:
                if (actElem.getMaxFunction () >= compElem.getMinFunction () &&
                    actElem.getMinFunction () <= compElem.getMaxFunction ())
                {
                    throw new ListException (
                        "The function handlers have overlapping function ranges: " +
                        actElem.toString () + " and " + compElem.toString ());
                } // if

                // check if the element is larger than the actual element:
                if (actElem.getMinFunction () > compElem.getMinFunction ())
                {
                    // exchange the elements:
                    this.p_elems.set (pos - 1, compElem);
                    this.p_elems.set (pos, actElem);
                } // if
                else
                {
                    // make the new element the actual element:
                    actElem = compElem;
                } // else
            } // for pos
        } // for elemCount
    } // sort


    /**************************************************************************
     * Check constraints for the elements within the container. <BR/>
     *
     * @throws  ListException
     *          Error when checking the constraints.
     */
    public void checkConstraints ()
        throws ListException
    {
        // drop unnecessary elements:
        this.dropUnnecessary ();
        // sort the list:
        this.sort ();
    } // checkConstraints


    /**************************************************************************
     * Drop unnecessary function handlers. <BR/>
     * Function handlers, which are replaced by other ones, are dropped from
     * the container.
     *
     * @throws  ListException
     *          An error occurred during the operation. <BR/>
     *          Possible reasons:
     *          A function handler should be replaced which does not exist.
     *          A function handler to be replaced has another function range
     *          (minfunc, maxfunc) than the function handler replacing it.
     */
    public void dropUnnecessary ()
        throws ListException
    {
        Vector<GeneralFunctionHandler> droppableElements =
            new Vector<GeneralFunctionHandler> (); // elements to be dropped
        IElementId replacedId = null;   // the id of the function handler
                                        // to be replaced
        GeneralFunctionHandler elem = null;   // the actual function handler
        GeneralFunctionHandler replacedElem = null; // the element to be replaced
        FunctionHandlerContainer wrongFunctionRanges =
            new FunctionHandlerContainer ();
                                        // function handlers with wrong function
                                        // ranges
        FunctionHandlerContainer wrongReplacedElems =
            new FunctionHandlerContainer ();
                                        // function handlers where the replaced
                                        // function handlers do not exist

// KR for debugging:
        // display the elements:
//        System.out.println ("unfiltered function handlers: " + this.toString ());

        // loop through all elements and find function handlers which replace
        // others:
        for (Iterator<GeneralFunctionHandler> iter = this.p_elems.iterator (); iter.hasNext ();)
        {
            // get the value:
            elem = iter.next ();
            replacedId = elem.getReplacedId ();

            // check if the function handler shall replace an existing one:
            if (replacedId != null)        // replace another handler?
            {
                // check if the element to be replaced exists:
                if ((replacedElem = this.get (replacedId)) != null)
                {
                    // check if the actual function handler implements the same
                    // function range:
                    if (elem.getMinFunction () == replacedElem.getMinFunction () &&
                        elem.getMaxFunction () == replacedElem.getMaxFunction ())
                    {
                        droppableElements.add (replacedElem);
                    } // if
                    else                // wrong function range
                    {
                        // add to the error list:
                        wrongFunctionRanges.add (elem);
                    } // else wrong function range
                } // if
                else                    // element does not exist
                {
                    // add to the error list:
                    wrongReplacedElems.add (elem);
                } // else element does not exist
            } // if replace another handler
        } // for iter

        // check for errors:
        if (wrongReplacedElems.size () > 0)
        {
            throw new ListException (
                "The following function handlers try to replace function" +
                " handlers which do not exist: " + wrongReplacedElems + ".");
        } // if

        if (wrongFunctionRanges.size () > 0)
        {
            throw new ListException (
                "The following function handlers have different function" +
                " ranges than the function handlers they replace: " +
                wrongFunctionRanges + ".");
        } // if

        // drop the replaced function handlers from the function handler
        // container:
        this.removeAll (droppableElements);

// KR for debugging:
        // display the elements:
//        System.out.println ("final function handlers: " + this.toString ());
    } // dropUnnecessary


//    /**************************************************************************
//     * Sets the dependencies of the files filled. <BR/>
//     */
// commented out by gw
// reason: call of empty functions
    
//    public void setDependentProperties ()
//    {
//        // loop through the function handlers and set the dependent properties
//        // for each of them:
//        for (Iterator<GeneralFunctionHandler> iter = this.iterator (); iter.hasNext ();)
//        {
//            // call the function:
//            (iter.next ()).setDependentProperties ();
//        } // for iter
//    } // setDependentProperties

    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     */
    public void setButtons (ButtonBarElement buttonBar)
    {
        // loop through the function handlers and set the buttons
        // for each of them:
        for (Iterator<GeneralFunctionHandler> iter = this.iterator (); iter.hasNext ();)
        {
            // call the function:
            (iter.next ()).setButtons (buttonBar);
        } // for iter
    } // setButtons

} // class FunctionHandlerContainer

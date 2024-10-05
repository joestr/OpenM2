/*
 * Class: IFunctionHandler.java
 */

// package:
package ibs.app.func;

//imports:
import ibs.service.list.IXMLElement;
import ibs.tech.html.ButtonBarElement;
import ibs.util.list.IElementId;
import ibs.util.list.ListException;


/******************************************************************************
 * This is the interface which shall be implemented from all function handlers.
 * <BR/>
 *
 * @version     $Id: IFunctionHandler.java,v 1.10 2007/07/20 12:41:51 kreimueller Exp $
 *
 * @author      Klaus, 15.11.2003
 ******************************************************************************
 */
public interface IFunctionHandler extends IXMLElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: IFunctionHandler.java,v 1.10 2007/07/20 12:41:51 kreimueller Exp $";



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
     * Evaluate the function to be performed. <BR/>
     *
     * @param   function    The function to be performed.
     * @param   values      The values for the function handler.
     *
     * @return  Function to be performed after this method. <BR/>
     *          {@link ibs.app.AppFunctions#FCT_NOFUNCTION AppFunctions.FCT_NOFUNCTION}
     *          if there is no function or the function was already performed.
     */
    public int evalFunction (int function, FunctionValues values);


    /**************************************************************************
     * Set the function number range handled by the function handler. <BR/>
     * If minFunc > maxFunc the values are changed.
     *
     * @param   minFunc     The minimum function number.
     * @param   maxFunc     The minimum function number.
     *
     * @throws  ListException
     *          One of the functions is not valid (&lt; 1).
     */
    public void setFunctionRange (int minFunc, int maxFunc)
        throws ListException;


    /**************************************************************************
     * Set the replacement value. <BR/>
     *
     * @param   replace The value.
     */
    public void setReplacedId (IElementId replace);


    /**************************************************************************
     * Set the minimum function number handled by the function handler. <BR/>
     *
     * @return  The function number.
     */
    public int getMinFunction ();


    /**************************************************************************
     * Set the maximum function number handled by the function handler. <BR/>
     *
     * @return  The function number.
     */
    public int getMaxFunction ();


    /**************************************************************************
     * Get the replacement value. <BR/>
     *
     * @return  The replace value.
     */
    public IElementId getReplacedId ();


    /**************************************************************************
     * Sets all possible buttons and constructs a buttonBar element. <BR/>
     *
     * @param   buttonBar   a ButtonBarElement Object where the buttons shall
     *                      be added to.
     *
     * @see GeneralFunctionHandler#setButton (ButtonBarElement, int, String, int, int)
     */
    public void setButtons (ButtonBarElement buttonBar);


    /**************************************************************************
     * Sets the dependencies of the files filled. <BR/>
     */
    public void setDependentProperties ();

} // interface IFunctionHandler

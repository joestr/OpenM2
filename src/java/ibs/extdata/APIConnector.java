/*
 * Class:QueryCreator_01.java
 */

// package:
package ibs.extdata;

// imports:
import ibs.di.DataElement;
import ibs.di.XMLViewer_01;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;


/******************************************************************************
 * APIConnector to hold connectionsdata for one m2 user to an
 * external system. <BR/>
 *
 * @version     $Id: APIConnector.java,v 1.8 2007/07/31 19:13:56 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 020517
 ******************************************************************************
 */
public class APIConnector extends XMLViewer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: APIConnector.java,v 1.8 2007/07/31 19:13:56 kreimueller Exp $";


    /**************************************************************************
     * Constructur.
     */
    public APIConnector ()
    {
        // nothing to do
    } // APIConnector


    /**************************************************************************
     * Change the data of a business object in the database using a given
     * operation. <BR/>
     *
     * @param   operation   Operation to be performed with the object.
     *
     * @exception   NoAccessException
     *              The user does not have access to this object to perform the
     *              required operation.
     * @exception   NameAlreadyGivenException
     *              An object with this name already exists. This exception is
     *              only raised by some specific object types which don't allow
     *              more than one object with the same name.
     *
     * @see #performChange()
     * @see #performChangeData(int)
     */
    public void performChange (int operation)
        throws NoAccessException, NameAlreadyGivenException
    {
        // change the data of the object:
        super.performChange (operation);

        // if apiConnector is changed, the apiConnectorPool has to be updated:
        ((APIConnectionPool) this.app.apiConPool).update (this);

    } // performChange


    /**************************************************************************
     * Reads the object data from an dataElement. <BR/>
     *
     * @param dataElement     the dataElement to read the data from
     *
     * @see ibs.bo.BusinessObject#readImportData
     */
    public void readImportData (DataElement dataElement)
    {
        super.readImportData (dataElement);

        // if apiConnector is imported, the apiConnectorPool has to be updated:
        ((APIConnectionPool) this.app.apiConPool).update (this);
    } // readImportData

} // class APIConnector

/*
 * Class: ExtDbRootContainer.java
 */

// package:
package ibs.extdata.db;

// imports:
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.OID;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.extdata.db.ExtDbObject;
import ibs.io.IOHelpers;
import ibs.service.user.User;


/******************************************************************************
 * ExtDbRootContainer to show Objects from external DB in m2. <BR/>
 *
 * @version     $Id: ExtDbRootContainer.java,v 1.7 2009/07/25 09:26:31 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ)
 ******************************************************************************
 */
public class ExtDbRootContainer extends ExtDbObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExtDbRootContainer.java,v 1.7 2009/07/25 09:26:31 kreimueller Exp $";


    /**************************************************************************
     * Creates a ExtDbRootContainer. <BR/>
     * initObject (...) has to be called for initialization of ExtDbRootContainer.
     * <BR/>
     */
    public ExtDbRootContainer ()
    {
        super ();
    } // ExtDbRootContainer


    /**************************************************************************
     * Creates a ExtDbRootContainer. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ExtDbRootContainer (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // XMLViewer_01


    /**************************************************************************
     * Gets the parameter extId. Which is the ID for the queries which
     * are executed for this objecttype. <BR/>
     */
    public void getParameters ()
    {
        XMLViewer_01 obj = null;
        String methodName = "ExtDbRootContainer.getParameters";

        try
        {
            // get the m2 MailClient Object -> to retrieve the connection
            // data from it
            obj = (XMLViewer_01) this.getObjectCache ().fetchObject (
                this.containerId, this.user, this.sess, this.env, false);
        } // try
        catch (ObjectNotFoundException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch
        catch (TypeNotFoundException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch
        catch (ObjectInitializeException e)
        {
            IOHelpers.showMessage (methodName,
                e, this.app, this.sess, this.env, true);
        } // catch

        ValueDataElement id = obj.dataElement.getValueElement ("ID");
        if (id != null)
        {
            // save id of current catalog in session
            this.p_virtualId = id.value;
        } // if
        else
        {
            IOHelpers.showMessage (
                this.getClass ().getName () + ".getParameters ERROR: " +
                "no VALUE FIELD='ID' exist in super object",
                this.app, this.sess, this.env);
        } // else
    } // getParameters

} // class ExtDbRootContainer

/*
 * Class: ExtDbNode.java
 */

// package:
package ibs.extdata.db;

// imports:
import ibs.bo.OID;
import ibs.bo.tab.TabConstants;
import ibs.di.DocumentTemplate_01;
import ibs.extdata.db.ExtDbObject;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.service.user.User;


/******************************************************************************
 * ExtDbNode to show Objects from external DB in m2. <BR/>
 * When calling on m2 - object using this class, the parameter extId with the
 * id of the current external Object (could be container or object) has to
 * be given as URL Parameter. <BR/>
 *
 * @version     $Id: ExtDbNode.java,v 1.7 2009/07/25 09:26:31 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ)
 ******************************************************************************
 */
public class ExtDbNode extends ExtDbObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ExtDbNode.java,v 1.7 2009/07/25 09:26:31 kreimueller Exp $";


    /**************************************************************************
     * Creates a ExtDbNode. <BR/>
     * initObject (...) has to be called for initialization of ExtDbNode.
     * <BR/>
     */
    public ExtDbNode ()
    {
        super ();
    } // ExtDbNode


    /**************************************************************************
     * Creates a ExtDbNode. <BR/>
     * initObject (...) has to be called for initialization of ExtDbNode.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ExtDbNode (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // ExtDbNode


    /**************************************************************************
     * Initializes a BusinessObject object. <BR/>
     * The compound object id is stored in the {@link #oid oid} property
     * of this object. <BR/>
     * The {@link #user user object} is also stored in a specific
     * property of this object to make sure that the user's context can be used
     * for getting his/her rights. <BR/>
     * {@link #env env} is initialized to the provided object. <BR/>
     * {@link #sess sess} is initialized to the provided object. <BR/>
     * {@link #app app} is initialized to the provided object. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     * @param   env     The actual environment object.
     * @param   sess    The actual session info object.
     * @param   app     The global application info object.
     */
    public void initObject (OID oid, User user, Environment env,
                            SessionInfo sess, ApplicationInfo app)
    {
        super.initObject (oid, user, env, sess, app);

        this.description = "";
        this.owner = new User ("");
        this.validUntil = new java.util.Date ();
        this.creationDate = new java.util.Date ();
        this.lastChanged = new java.util.Date ();
        this.dataElement = ((DocumentTemplate_01) this.typeObj.getTemplate ())
            .getTemplateDataElement ();
        this.dataElement.name = "";
        this.dataElement.description = "";
    } // initObject


    /**************************************************************************
     * Show the object, i.e. its properties. <BR/>
     * The properties are gotten from the database (with rights checked) and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     */
    public void showInfo (int representationForm)
    {
        super.showInfo (representationForm);

        this.getUserInfo ().history.add (this.oid, TabConstants.TAB_NONE, this.name, this.icon, this.p_virtualId);
    } // showInfo


    /**************************************************************************
     * Gets the parameter extId. Which is the ID for the queries which
     * are executed for this objecttype. <BR/>
     */
    public void getParameters ()
    {
        String str = null;

        // name
        if ((str = this.env.getStringParam ("extId")) != null)
        {
            this.p_virtualId = str;
        } // if
        else if ((str = this.getUserInfo ().history.getExtId ()) != null)
        {
            this.p_virtualId = str;
        } // else if
        else
        {
            IOHelpers.showMessage (
                "ExtDbNode.getParameters ERROR: no Parameter extId given",
                this.app, this.sess, this.env);
        } // if
    } // getParameters

} // class ExtDbNode

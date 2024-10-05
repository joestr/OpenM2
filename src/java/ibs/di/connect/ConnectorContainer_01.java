/*
 * Class: ConnectorContainer_01.java
 */

// package:
package ibs.di.connect;

// imports:
//TODO: unsauber
import ibs.bo.Container;
//TODO: unsauber
import ibs.bo.OID;
//TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type ConnectorContainer with
 * version 01. <BR/>
 *
 * @version     $Id: ConnectorContainer_01.java,v 1.15 2009/07/24 23:24:29 kreimueller Exp $
 *
 * @author      Harald Buzzi (HB), 991208
 ******************************************************************************
 */
public class ConnectorContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ConnectorContainer_01.java,v 1.15 2009/07/24 23:24:29 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * ConnectorContainer_01. <BR/>
     */
    public ConnectorContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ConnectorContainer_01


    /**************************************************************************
     * Creates a ConnectorContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ConnectorContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ConnectorContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
/*
        // set which types are allowed in the Container
        String[] typeIds = {Integer.toString (createTVersionId (Types.TYPE_FileConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_FTPConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_MailConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_HTTPConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_EDISwitchConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_HTTPScriptConnector_01)),
                            Integer.toString (createTVersionId (Types.TYPE_SAPBCXMLRFCConnector_01)),
                            };
        String[] typeNames = {Types.TN_FileConnector_01,
                              Types.TN_FTPConnector_01,
                              Types.TN_MailConnector_01,
                              Types.TN_HTTPConnector_01,
                              Types.TN_EDISwitchConnector_01,
                              Types.TN_HTTPScriptConnector_01,
                              Types.TN_SAPBCXMLRFCConnector_01,
                             };
        this.typeIds = typeIds;
        this.typeNames = typeNames;
*/
    } // initClassSpecifics

} // ConnectorContainer_01

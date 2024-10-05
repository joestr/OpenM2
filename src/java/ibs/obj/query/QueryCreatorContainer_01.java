/*
 * Class: QueryCreatorContainer_01.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: QueryCreatorContainer_01.java,v 1.7 2009/07/24 10:21:03 kreimueller Exp $
 *
 * @author      Andreas Jansa (AJ), 001013
 ******************************************************************************
 */
public class QueryCreatorContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: QueryCreatorContainer_01.java,v 1.7 2009/07/24 10:21:03 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class QueryCreatorContainer_01.
     * <BR/>
     */
    public QueryCreatorContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // QueryCreatorContainer_01


    /**************************************************************************
     * Creates a QueryCreatorContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public QueryCreatorContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:
    } // QueryCreatorContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics

} // class QueryCreatorContainer_01

/*
 * Class: PersonUserContainer_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type PersonUserContainer with version 01.
 * <BR/>
 *
 * @version     $Id: PersonUserContainer_01.java,v 1.6 2009/07/25 00:38:46 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980911
 ******************************************************************************
 */
public class PersonUserContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PersonUserContainer_01.java,v 1.6 2009/07/25 00:38:46 kreimueller Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * This constructor creates a new instance of the class PersonUserContainer_01.
     * <BR/>
     */
    public PersonUserContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // PersonUserContainer_01


    /**************************************************************************
     * Creates a PersonUserContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PersonUserContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // PersonUserContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // nothing to do
    } // initClassSpecifics


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_SEARCH,
//          Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class PersonUserContainer_01

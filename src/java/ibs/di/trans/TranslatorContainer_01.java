/*
 * Class: TranslatorContainer_01.java
 */

// package:
package ibs.di.trans;

// imports:
//KR TODO: unsauber
import ibs.bo.Container;
//KR TODO: unsauber
import ibs.bo.OID;
//KR TODO: unsauber
import ibs.service.user.User;


/******************************************************************************
 * This class represents one object of type ImportScriptContainer with
 * version 01. <BR/>
 *
 * @version     $Id: TranslatorContainer_01.java,v 1.8 2009/07/24 23:22:19 kreimueller Exp $
 *
 * @author      Harald Buzzi (HB), 991202
 ******************************************************************************
 */
public class TranslatorContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TranslatorContainer_01.java,v 1.8 2009/07/24 23:22:19 kreimueller Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class
     * TranslatorContainer_01.
     * <BR/>
     */
    public TranslatorContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // TranslatorContainer_01


    /**************************************************************************
     * Creates a TranslatorContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public TranslatorContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // TranslatorContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
/*
        // set which types are allowed in the Container
        String[] typeIds = {
                    Integer.toString (createTVersionId (Types.TYPE_Translator_01))
        };
        String[] typeNames = {Types.TN_Translator_01
        };
        this.typeIds = typeIds;
        this.typeNames = typeNames;

        // set majorContainer true
        isMajorContainer = true;
*/
    } // initClassSpecifics

} // class TranslatorContainer_01

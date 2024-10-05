/*
 * Class: DiscussionElement_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.session.ApplicationInfo;

import m2.bbd.BbdTypeConstants;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ReferenzContainer. <BR/>
 *
 * @version     $Id: DiscussionElement_01.java,v 1.10 2009/09/10 10:19:57 kreimueller Exp $
 *
 * @author      Keim Christine (CK), 980508
 ******************************************************************************
 */
public class DiscussionElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DiscussionElement_01.java,v 1.10 2009/09/10 10:19:57 kreimueller Exp $";


    /**
     * PosNoPath. <BR/>
     */
    public String posNoPath;


    /**************************************************************************
     * Creates a DiscussionElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public DiscussionElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's private properties:

        // initialize the instance's public properties:
        this.posNoPath = null;
    } // DiscussionElement_01


    /**************************************************************************
     * Creates a DiscussionElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public DiscussionElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's private properties:

        // set the instance's public properties:
        this.posNoPath = null;
    } // DiscussionElement_01


    /**************************************************************************
     * Check if the object is exportable. <BR/>
     *
     * @param   appInfo The application info object which can be used to get the
     *                  type information.
     *
     * @return  <CODE>true</CODE> if the object is exportable,
     *          <CODE>false</CODE> otherwise.
     */
    @Override
    public boolean isExportable (ApplicationInfo appInfo)
    {
// HINT: KR should be checked.
        // a discussion entry is not exportable:
        return !(this.oid.tVersionId ==
            BOHelpers.getTypeCache ()
                .getTVersionId (BbdTypeConstants.TC_DiscEntry));
    } // isExportable

} // class DiscussionElement_01

/*
 * Class: PublicationContainerElement_01.java
 */

// package:
package m2.version.publish;

// imports:
import ibs.bo.OID;

import m2.version.VersionContainerElement_01;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * VersionContainerElement_01. <BR/>
 *
 * @version     $Id: PublicationContainerElement_01.java,v 1.4 2007/07/10 21:01:32 kreimueller Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class PublicationContainerElement_01 extends VersionContainerElement_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PublicationContainerElement_01.java,v 1.4 2007/07/10 21:01:32 kreimueller Exp $";


    /**************************************************************************
     * Creates a XMLViewerContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public PublicationContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
    } // PVersionContainerElement_01


    /**************************************************************************
     * Creates a VersionContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public PublicationContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
    } // PVersionContainerElement_01

} // class PVersionContainerElement_01

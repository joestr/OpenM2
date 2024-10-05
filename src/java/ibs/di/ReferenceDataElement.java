/*
 * Class: ReferenceDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;


/******************************************************************************
 * The ReferenceDataElement hold the information of an VALUES section from the
 * XML import file. <BR/>
 *
 * @version     $Id: ReferenceDataElement.java,v 1.8 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 000630
 ******************************************************************************
 */
public class ReferenceDataElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReferenceDataElement.java,v 1.8 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     * Type of container setting. <BR/>
     */
    public String containerType = null;

    /**
     *  Container name or oid. <BR/>
     */
    public String containerId = null;

    /**
     *  Domain of an external id. <BR/>
     */
    public String containerIdDomain = null;

    /**
     *  name of a tab of the container. <BR/>
     */
    public String containerTabName = null;


    /**************************************************************************
     * Creates an ReferenceDataElement. <BR/>
     */
    public ReferenceDataElement ()
    {
        // nothing to do
    } // ReferenceDataElement


    /**************************************************************************
     * Creates an ReferenceDataElement. <BR/>
     *
     * @param containerType     type of container setting
     * @param containerId       container name of oid
     * @param containerIdDomain domain of an external id
     * @param containerTabName  name of a tab of the container
     */
    public ReferenceDataElement (String containerType,
                                 String containerId,
                                 String containerIdDomain,
                                 String containerTabName)
    {
        // call constructor of super class ObjectReference:
        this.containerType = containerType;
        this.containerId = containerId;
        this.containerIdDomain = containerIdDomain;
        this.containerTabName = containerTabName;
    } // ReferenceDataElement

} // class ReferenceDataElement

/*
 * Class: ImportScriptElement.java
 */

// package:
package ibs.di.imp;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.OID;


/******************************************************************************
 * The ImportScriptElement hold the information of an import Script XML
 * document. The import of objects can be controlled by the use of an import
 * script. The import script at this stage has some still limited
 * functions. <BR/>
 *
 * @version     $Id: ImportScriptElement.java,v 1.14 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 990119
 ******************************************************************************
 */
public class ImportScriptElement extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ImportScriptElement.java,v 1.14 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * reference to a objecttype name. <BR/>
     */
    public String typeRef;

    /**
     * reference to a objecttype code. <BR/>
     */
    public String typeCodeRef;

    /**
     * type of operation. <BR/>
     */
    public String operationType;

    /**
     * name of custom operation. used to trigger a hardcoded import function. <BR/>
     */
    public String operationName;

    /**
     * type of container. <BR/>
     */
    public String containerType;

    /**
     * container name or id or path depending on containerType. <BR/>
     */
    public String containerId;

    /**
     * domain of an external container id. <BR/>
     */
    public String containerIdDomain;

    /**
     * name of a tab of the container. <BR/>
     */
    public String containerTabName;

    /**
     * OID of the destination container. <BR/>
     * This should be cached in the import script element in order to
     * avoid multiple resolution. <BR/>
     */
    public OID containerOid;


    /**************************************************************************
     * Creates an ImportScriptElement. <BR/>
     */
    public ImportScriptElement ()
    {
        // call constructor of super class ObjectReference:
        this.typeRef        = null;
        this.typeCodeRef    = null;
        this.operationType  = null;
        this.operationName  = null;
        this.containerType  = null;
        this.containerId    = null;
        this.containerIdDomain  = null;
        this.containerTabName   = null;
    } // ImportScriptElement


    /**************************************************************************
     * Creates an ImportScriptElement. <BR/>
     *
     * @param typeRef           typename reference
     * @param typeCodeRef       typecode reference
     * @param operationType     type of operation to perform
     * @param operationName     name of custom oepration
     * @param containerType     type of container id source
     * @param containerId       ID or name of container
     * @param containerIdDomain domain of an external container id
     * @param containerTabName  name of a tab of the container
     *
     */
    public ImportScriptElement (String typeRef, String typeCodeRef,
                                String operationType, String operationName,
                                String containerType, String containerId,
                                String containerIdDomain, String containerTabName)
    {
        // set the properties
        this.typeRef            = typeRef;
        this.typeCodeRef        = typeCodeRef;
        this.operationType      = operationType;
        this.operationName      = operationName;
        this.containerType      = containerType;
        this.containerId        = containerId;
        this.containerIdDomain  = containerIdDomain;
        this.containerTabName   = containerTabName;
    } // ImportScriptElement

} // ImportScriptElement

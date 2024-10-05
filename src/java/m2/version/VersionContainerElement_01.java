/*
 * Class: VersionContainerElement_01.java
 */

// package:
package m2.version;

// imports:
import ibs.bo.BOPathConstants;
import ibs.bo.OID;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewerContainerElement_01;
import ibs.io.Environment;
import ibs.tech.html.ImageElement;
import ibs.tech.html.RowElement;

import m2.version.Version_01;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * VersionContainerElement_01. <BR/>
 *
 * @version     $Id: VersionContainerElement_01.java,v 1.7 2010/04/07 13:37:14 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class VersionContainerElement_01 extends XMLViewerContainerElement_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: VersionContainerElement_01.java,v 1.7 2010/04/07 13:37:14 rburgermann Exp $";


    /**************************************************************************
     * Creates a XMLViewerContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public VersionContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
    } // VersionContainerElement_01


    /**************************************************************************
     * Creates a VersionContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid     Value for the compound object id.
     */
    public VersionContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
    } // VersionContainerElement_01


    /**************************************************************************
     * Represent this object to the user. This method overrides the the method
     * show from XMLViewerContainerElement_01. <BR/>
     *
     * @param   classId Class for stylesheet for this row.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        // show new icon
        String oldName = this.name;

        // actual element is a master
        ValueDataElement vde = this.dataElement.getValueElement (Version_01.ATTR_MASTER);
        boolean isMaster = new Boolean (vde.value).booleanValue ();

///////////////////////
// BM: HACK HACK HACK BEGIN
// the name of the actual object is changed. the icon is added to the master
// name to show the master object
///////////////////////
        // if the actual element is a master then include the icon
        if (isMaster)
        {
            ImageElement img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Master.gif");
            this.name += img.toString ();
        } // if

        RowElement tr = super.show (classId, env);

        // to prevent from side effects set the name back to the old value
        this.name = oldName;
///////////////////////
// BM: HACK HACK HACK END
///////////////////////
        return  tr;
    } // show

} // class VersionContainerElement_01

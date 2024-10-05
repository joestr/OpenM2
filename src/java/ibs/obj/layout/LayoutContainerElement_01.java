/*
 * Class: LayoutContainerElement_01.java
 */

// package:
package ibs.obj.layout;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.LayoutConstants;
import ibs.io.LayoutElement;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * LayoutContainer. <BR/>
 *
 * @version     $Id: LayoutContainerElement_01.java,v 1.8 2010/04/07 13:37:15 rburgermann Exp $
 *
 * @author      Keim Christine (KR), 981221
 ******************************************************************************
 */
public class LayoutContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LayoutContainerElement_01.java,v 1.8 2010/04/07 13:37:15 rburgermann Exp $";


    ///////////////////////////////////////////////////////////////////////////
    // properties
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Path, where the Layout can be found. <BR/>
     */
    public String path = "";

    /**
     * Name of the file, where the frameset is loaded
     */
    public String frameset = "";


    /**
     * Elements of the Layout. <BR/>
     */
    public LayoutElement[] elems = new LayoutElement[LayoutConstants.STYLECOUNT];

    /**
     * Is this the default layout? <BR/>
     * Default: <CODE>false</CODE>
     */
    protected boolean p_isDefault = false;

    /**
     * Number of specific attributes. <BR/>
     */
    private static final int SPECIFIC_ATTRIBUTES = 1;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Creates a LayoutContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public LayoutContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
//        elems = new LayoutElement[4];
    } // LayoutContainerElement_01


    /**************************************************************************
     * Creates a LayoutContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public LayoutContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // set the instance's public properties:
//        elems = new LayoutElement[4];

    } // LayoutContainerElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     The CSS class to be set for the actual element.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        GroupElement nameGroup = null;  // the name group
        RowElement tr;                  // table row
        TableDataElement td = null;     // table cell

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS.length + 1 +
                LayoutContainerElement_01.SPECIFIC_ATTRIBUTES);
        } // if
        else
        {
            tr = new RowElement (BOListConstants.LST_HEADINGS_REDUCED.length +
                1 + LayoutContainerElement_01.SPECIFIC_ATTRIBUTES);
        } // else

        tr.classId = classId;

        // display the isNew flag:
        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        // create the name group:
        nameGroup = this.getNameGroup ();
        td = new TableDataElement (nameGroup);
        td.classId = classId;
        tr.addElement (td);

        if (this.p_isDefault)
        {
            ImageElement img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Master.gif");
            nameGroup.addElement (img);
        } // if

        if (this.showExtendedAttributes)
        {
            // show the extended attributes:
            this.showExtendedAttributes (tr, classId);
        } // if

        return tr;                      // return the constructed row
    } // show

} // class LayoutContainerElement_01

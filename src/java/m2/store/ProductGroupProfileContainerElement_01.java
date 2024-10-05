/*
 * Class: ProductGroupProfileContainerElement_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ProductGroupProfileContainer. <BR/>
 *
 * @version     $Id: ProductGroupProfileContainerElement_01.java,v 1.9 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 980908
 ******************************************************************************
 */
public class ProductGroupProfileContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductGroupProfileContainerElement_01.java,v 1.9 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * Name of the company for that product group. <BR/>
     */
    public int sortKey;


    /**************************************************************************
     * Creates a ProductGroupProfileContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public ProductGroupProfileContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // ProductGroupProfileContainerElement_01


    /**************************************************************************
     * Creates a ProductGroupProfileContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public ProductGroupProfileContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // ProductGroupProfileContainerElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     class for the stylesheet.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        RowElement tr = new RowElement (3);
        tr.classId = classId;
        TableDataElement td = null;

        // set isNew flag
        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (
                BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        td.width = BOListConstants.LST_NEWCOLWIDTH;

        td.classId = classId;

        tr.addElement (td);

        // icon of the element
        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        // name of the element as link
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());
        nameGroup.addElement (new TextElement (this.name));
        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));

        td.classId = classId;

        tr.addElement (td);

        // column for last changed date
        td = new TableDataElement (new TextElement (DateTimeHelpers
            .dateTimeToString (this.lastChanged)));

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

} // class ProductGroupProfileContainerElement_01

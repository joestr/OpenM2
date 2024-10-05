/*
 * Class: CatalogElement_01.java
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
 * Catalog. <BR/>
 *
 * @version     $Id: CatalogElement_01.java,v 1.9 2010/04/07 13:37:07 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 980908
 ******************************************************************************
 */
public class CatalogElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CatalogElement_01.java,v 1.9 2010/04/07 13:37:07 rburgermann Exp $";


    /**
     * Name of the company. <BR/>
     */
    public String company = null;


    /**************************************************************************
     * Creates a CatalogElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public CatalogElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // CatalogElement_01


    /**************************************************************************
     * Creates a CatalogElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public CatalogElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // CatalogElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     class for the style sheet.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        RowElement tr;
        TableDataElement td = null;

        if (!this.showExtendedAttributes)
        {
            tr = new RowElement (3);
        } // if
        else
        {
            tr = new RowElement (5);
        } // else

        tr.classId = classId;

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
        td.classId = classId;

        td.width = BOListConstants.LST_NEWCOLWIDTH;
        tr.addElement (td);

        // icon of the element
        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if no icon provided
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        // name of the element as link
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                                    BOPathConstants.PATH_OBJECTICONS +
                                    "Referenz.gif");
            nameGroup.addElement (img);
            nameGroup.addElement (new TextElement (this.name));
            td = new TableDataElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.linkedObjectId)));
        } // if
        else
        {
            nameGroup.addElement (new TextElement (this.name));
            td = new TableDataElement (new LinkElement (nameGroup,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));
        } // else no link

        td.classId = classId;

        tr.addElement (td);

        // column for description
        if (this.description != null && this.description.trim ().length () > 0)
        {
            td = new TableDataElement (new TextElement (this.description));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else

        td.classId = classId;

        tr.addElement (td);


        if (this.showExtendedAttributes)
        {
            // type
            td = new TableDataElement (new TextElement (this.typeName));
            td.classId = classId;

            tr.addElement (td);

            // date when object last changed
            td = new TableDataElement (new TextElement (DateTimeHelpers
                .dateTimeToString (this.lastChanged)));
            td.classId = classId;

            td.nowrap = true;
            tr.addElement (td);
        } // else

        return tr;                      // return the constructed row
    } // show

} // class CatalogElement_01

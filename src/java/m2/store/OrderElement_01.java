/*
 * Class: OrderElement_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.Helpers;


/******************************************************************************
 * This class contains all necessary properties of one element of a
 * ShoppingCart or a Order. <BR/>
 *
 * @version     $Id: OrderElement_01.java,v 1.12 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW) 980908
 ******************************************************************************
 */
public class OrderElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OrderElement_01.java,v 1.12 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * The oid of the product this entry refers to. <BR/>
     */
    public OID productOid = null;
    /**
     * The oid of the product this entry refers to. <BR/>
     */
    public String productDescription = null;
    /**
     * Number of products ordered. <BR/>
     */
    public int quantity = 0;
    /**
     * The smallest unit of quantity a user may order. <BR/>
     */
    public int unitOfQty = 1;
    /**
     * The packing unit of the product. <BR/>
     */
    public String packingUnit = null;
    /**
     * The currency used for the price. <BR/>
     */
    public String priceCurrency = "";
    /**
     * The price of the product the user has to pay. <BR/>
     */
    public long price = 0;
    /**
     * The total cost = unitOfQty * qty * cost. <BR/>
     */
    public long totalPrice = 0;
    /**
     * The productno of the product. <BR/>
     */
    public String productno = null;


    /**************************************************************************
     * Creates a OrderElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public OrderElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // OrderElement_01


    /**************************************************************************
     * Creates a OrderElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid     Value for the compound object id.
     */
    public OrderElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);
        // initialize the instance's private properties:
        // initialize the instance's public properties:
    } // OrderElement_01


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     Class for the stylesheet.
     * @param   env         The current environment 
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        RowElement tr = new RowElement (6);
        tr.classId = classId;
        TableDataElement td = null;

        td = new TableDataElement (new BlankElement ());
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

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
        nameGroup.addElement (new TextElement (this.name));

        td = new TableDataElement (nameGroup);
        td.classId = classId;

        tr.addElement (td);



        // column for unitOfQty
        td = new TableDataElement (new TextElement ("" + this.unitOfQty));

        td.classId = classId;

        tr.addElement (td);

        // column for packing unit
        if (this.packingUnit != null && this.packingUnit.trim ().length () > 0)
        {
            td = new TableDataElement (new TextElement ("" + this.packingUnit));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else

        td.classId = classId;

        tr.addElement (td);

        // column for quantity
        td = new TableDataElement (new TextElement ("" + this.quantity));
        td.classId = classId;

        tr.addElement (td);


        // column for total cost
        td = new TableDataElement (new TextElement (
                    this.priceCurrency + " " +
                    Helpers.moneyToString (this.quantity * this.unitOfQty * this.price)));

        td.classId = classId;

        tr.addElement (td);

        return tr;                      // return the constructed row
    } // show

    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   cols        The number of columns.
     * @param   withLink    Shall there be a link?
     * @param   classId     The CSS class id.
     *
     * @return  The constructed row element for a table.
     */
    public RowElement showPosition (int cols, boolean withLink, String classId)
    {

        RowElement tr = new RowElement (cols);
        tr.classId = classId;
        TableDataElement td = null;
//        String altText = "";            // text to be shown when the mouse
//                                        // pointer stays over the object
        TextElement text;

        // column for quantity
        td = new TableDataElement (new TextElement ("" + this.quantity));
        tr.addElement (td);
        // column for unitOfQty
        td = new TableDataElement (new TextElement ("" + this.unitOfQty));
        tr.addElement (td);
        // column for packing unit
        td = new TableDataElement (new TextElement ("" + this.packingUnit));
        tr.addElement (td);

        // name of the element as link
        GroupElement productGroup = new GroupElement ();
        LinkElement link;

        // add the productno
        if (this.productno != null)
        {
            text = new TextElement (this.productno + IE302.TAG_NEWLINE);
            productGroup.addElement (text);
        } // if
        // add the name of the product
        text = new TextElement (this.name);
        if (withLink)
        {
            link = new LinkElement (text,
                IOHelpers.getShowObjectJavaScriptUrl ("" + this.productOid));
            productGroup.addElement (link);
        } // if
        else
        {
            productGroup.addElement (text);
        } // else

        // add the product description
        text = new TextElement (" " + this.productDescription);

        productGroup.addElement (text);
        td = new TableDataElement (productGroup);
        tr.addElement (td);

        // column for price per unit
        td = new TableDataElement (
                new TextElement (
                    this.priceCurrency + " " +
                    Helpers.moneyToString (this.price)));
        tr.addElement (td);

        // column for total cost of position
        this.totalPrice = this.quantity * this.unitOfQty * this.price;
        td = new TableDataElement (
                new TextElement (
                    Helpers.moneyToString (this.totalPrice)));
        tr.addElement (td);

        return tr;                      // return the constructed row
    } //

    /**************************************************************************
     * Writes the object data to a DataElement. <BR/>
     *
     * @param   dataElement The dataElement to write the data to.
     *
     * @see ibs.bo.BusinessObject#writeExportData
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        //super.writeExportData (dataElement);
        dataElement.name = this.name;
        dataElement.description = this.description;

        // set the type specific values
        dataElement.setExportValue ("productOid", this.productOid.toString ());
        dataElement.setExportValue ("productDescription", this.productDescription);
        dataElement.setExportValue ("quantity", this.quantity);
        dataElement.setExportValue ("unitOfQty", this.unitOfQty);
        dataElement.setExportValue ("packingUnit", this.packingUnit);
        dataElement.setExportValue ("priceCurrency", this.priceCurrency);
        dataElement.setExportValue ("price", this.price);
        dataElement.setExportValue ("totalPrice", this.totalPrice);
        dataElement.setExportValue ("productno", this.productno);
    } // writeExportData

} // class OrderElement_01

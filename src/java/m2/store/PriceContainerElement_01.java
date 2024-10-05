/*
 * Class: PriceContainerElement_01.java
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
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.IE302;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.Helpers;

import m2.store.ProductCode;
import m2.store.StoreTokens;

import java.util.Vector;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * PriceContainer. <BR/>
 *
 * @version     $Id: PriceContainerElement_01.java,v 1.8 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW), 980602
 ******************************************************************************
 */
public class PriceContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PriceContainerElement_01.java,v 1.8 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * Currency of cost. <BR/>
     */
    public String priceCurrency;
    /**
     * Price of element. <BR/>
     */
    public long price = 0;
    /**
     * Currency of cost. <BR/>
     */
    public String costCurrency;
    /**
     * Cost of element. <BR/>
     */
    public long cost = 0;
    /**
     * Codes for which this price is valid. <BR/>
     */
    public Vector<ProductCode> codes = null;

    /**
     * Minimum order-quantity for wich this price is valid.
    */
    public int  qty = 1;


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public PriceContainerElement_01  ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
    } // PriceContainerElement_01 ()


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public PriceContainerElement_01  (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
    } // PriceContainerElement_01


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
        TextElement text = null;

        RowElement tr = new RowElement (5);
        tr.classId = classId;
        TableDataElement td = null;

        td = new TableDataElement (new BlankElement ());
        td.classId = classId;

        td.width = BOListConstants.LST_NEWCOLWIDTH;
        tr.addElement (td);

        // show object icon
        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if no icon provided
        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);

        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);

        // show the price
        nameGroup.addElement (new BlankElement ());
        text = new TextElement (this.priceCurrency + IE302.HCH_NBSP +
            Helpers.moneyToString (this.price));
        nameGroup.addElement (text);
        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid)));

        td.classId = classId;

        tr.addElement (td);

        // cost
        text = new TextElement (this.costCurrency + IE302.HCH_NBSP +
            Helpers.moneyToString (this.cost));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        text = new TextElement (Integer.toString (this.qty));
        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);


        // column for the product codes for which this price is
        // valid
        if (this.codes != null && this.codes.size () > 0)
        {
            ProductCode code = null;
            StringBuffer sb = new StringBuffer ();
            int size = this.codes.size ();

            for (int i = 0; i < size; i++)
            {
                code = this.codes.elementAt (i);
                sb.append (code.name.toString ());
                sb.append (": ");
                sb.append (code.toString (", "));
                sb.append (IE302.TAG_NEWLINE);
            } // for
            text = new TextElement (sb.toString ());
        } // if
        else
        {
            text = new TextElement (
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_SAMEPRICE, env));
        } // else

        td = new TableDataElement (text);

        td.classId = classId;

        tr.addElement (td);

        // quantity
        return tr;                      // return the constructed row
    } // show

} // class PriceContainerElement_01

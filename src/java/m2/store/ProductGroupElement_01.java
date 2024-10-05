/*
 * Class: ProductGroupElement_01.java
 */

// package:
package m2.store;

// imports:
import ibs.app.CssConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.ContainerElement;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.SpanElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.Helpers;
import ibs.util.UtilConstants;

import m2.store.StoreConstants;
import m2.store.StoreFunctions;
import m2.store.StoreTokens;

import java.util.Vector;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ProductGroupElement_01. <BR/>
 *
 * @version     $Id: ProductGroupElement_01.java,v 1.16 2010/04/07 13:37:06 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 980602
 ******************************************************************************
 */
public class ProductGroupElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductGroupElement_01.java,v 1.16 2010/04/07 13:37:06 rburgermann Exp $";


    /**
     * Name of the thumbnail of the container element. <BR/>
     */
    public String productNo = "";
    /**
     * Flag is true when the thumnail is just the smaller version of the
     * picture. <BR/>
     */
    public boolean thumbAsImage = false;
    /**
     * Name of the thumbnail of the container element. <BR/>
     */
    public String thumbnail = "";
    /**
     * Name of the image of the container element. <BR/>
     */
    public String image = "";
    /**
     * Additional path info. <BR/>
     */
    public String path = null;

    /**
     * Min buy prices of the product (content objecttype Long). <BR/>
     */
    public Vector<Long> minBuyPrice = new Vector<Long> ();
    /**
     * Max buy prices of the product (content objecttype Long). <BR/>
     */
    public Vector<Long> maxBuyPrice = new Vector<Long> ();
    /**
     * The currencies used for the prices. (content objecttype String). <BR/>
     */
    public Vector<String> costCurrency = new Vector<String> ();
    /**
     * Min sales prices of the product (content objecttype Long). <BR/>
     */
    public Vector<Long> minSalesPrice = new Vector<Long> ();
    /**
     * Max sales prices of the product (content objecttype Long). <BR/>
     */
    public Vector<Long> maxSalesPrice = new Vector<Long> ();
    /**
     * The currencies used for the prices. (content objecttype String). <BR/>
     */
    public Vector<String> priceCurrency = new Vector<String> ();

    /**
     * The actual session info object. <BR/>
     */
    private SessionInfo sess = null;


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
        RowElement row = new RowElement (4);
        row.classId = classId;
        TableDataElement tde = null;
        TextElement te = null;
        GroupElement ge = new GroupElement ();
        NewLineElement nle = new NewLineElement ();
        SpanElement sp;

        // add the isnew indicator to the row
        if (this.isNew)
        {
            tde = new TableDataElement (new ImageElement (
                BOPathConstants.PATH_GLOBAL + "new.gif"));
            tde.valign = IOConstants.ALIGN_MIDDLE;
            tde.alignment = IOConstants.ALIGN_CENTER;
        } // if
        else
        {
            tde = new TableDataElement (new BlankElement ());
        } // else
        tde.width = BOListConstants.LST_NEWCOLWIDTH;
        row.addElement (tde);


        // add product number
        te = new TextElement (this.productNo);
        sp = new SpanElement ();
        sp.classId = StoreConstants.CLASS_PRODUCTGROUP_NUMBER;
        sp.addElement (te);
        ge.addElement (sp);

        ge.addElement (nle);


        // add image for link to groupelement
        if (this.isLink)                // object is a link to another object?
        {
            ImageElement img =
                new ImageElement (this.layoutpath +
                                  BOPathConstants.PATH_OBJECTICONS +
                                  "Referenz.gif");
            ge.addElement (img);
        } // if object is a link to another object


        // add the name of the product
        te = new TextElement (this.name);
        sp = new SpanElement ();
        sp.classId = StoreConstants.CLASS_PRODUCTGROUP_NAME;
        sp.addElement (te);

        String oidStr = null;
        if (this.isLink)                // object is a link to another object?
        {
            oidStr = "" + this.linkedObjectId;
        } // if object is a link to another object
        else                            // object is no link
        {
            oidStr = "" + this.oid;
        } // else object is no link
        ge.addElement (new LinkElement (sp, IOHelpers
            .getShowObjectJavaScriptUrl (oidStr)));

        ge.addElement (nle);

        // show the description
        if (this.description != null && this.description.length () > 0)
        {
            te = new TextElement (this.description);
            sp = new SpanElement ();
            sp.classId = StoreConstants.CLASS_PRODUCTGROUP_DESCR;
            sp.addElement (te);
            ge.addElement (sp);

            ge.addElement (nle);
        } // if

        // get count of Prices
        int count = this.minBuyPrice.size ();

        // loop through all different prices and currencies
        for (int i = 0; i < count; i++)
        {
            this.showPrice (ge, i, env);
        } // for i

        // add the product description to the row
        tde = new TableDataElement (ge);
       // tde.bgcolor = bgcolor;
        tde.width = "90%";
        tde.alignment = IOConstants.ALIGN_LEFT;
        row.addElement (tde);

        // add the shopping cart icon
        ImageElement ie;
        ie = new ImageElement (BOPathConstants.PATH_GLOBAL + "shoppingCart.gif");
        ie.alt = 
            MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                StoreTokens.ML_PUTIN_CART, env);

        LinkElement link = new LinkElement (ie, IOConstants.URL_JAVASCRIPT +
            "top.loadOrderWindowInList (" +
            StoreFunctions.FCT_LOADORDER_FRAMESET + ",'" + oidStr + "');");


        tde = new TableDataElement (link);
        //tde.bgcolor = bgcolor;
        tde.valign = IOConstants.ALIGN_MIDDLE;
        tde.alignment = IOConstants.ALIGN_CENTER;
        row.addElement (tde);
        String img;
        String pathExtension = "";
        if (this.path != null && this.path.trim ().length () > 0)
        {
            pathExtension = this.path + "//";
        } // if
        String pathStart = null;

        // add the thumbnail to the row:
        if (this.thumbAsImage)
        {
            img = this.image;
            pathStart = BOPathConstants.PATH_UPLOAD_PICTURES;
        } // if
        else
        {
            img = this.thumbnail;
            pathStart = BOPathConstants.PATH_UPLOAD_THUMBS;
        } // else
        ie = new ImageElement (this.sess.home + pathStart + pathExtension + img);
        ie.alt = this.name;
        ie.width = "" + StoreConstants.CONST_WIDTH_THUMBNAIL;
        link = new LinkElement (ie,
            IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ()));
        tde = new TableDataElement (link);

        // check if image is empty
        if (img == null || img.trim ().length () == 0)
        {
            tde = new TableDataElement (new BlankElement ());
        } //if

        // tde.bgcolor = bgcolor;
        tde.width = "" + (StoreConstants.CONST_WIDTH_THUMBNAIL + 20);
        tde.valign = IOConstants.ALIGN_MIDDLE;
        tde.alignment = IOConstants.ALIGN_CENTER;
        row.addElement (tde);

        return row;
        // return the constructed cell
    } // show


    /**************************************************************************
     * Show a price incl. currencies to the user. <BR/>
     *
     * @param   ge      Group element to which to add the output.
     * @param   index   Index within price list.
     * @param   env     The current environment
     */
    private void showPrice (GroupElement ge, int index, Environment env)
    {
        TextElement te = null;
        NewLineElement nle = new NewLineElement ();
        SpanElement sp;
        String str = "";

        // initialize prices to PRICE_NOT_SET
        long minBuyPrice = StoreConstants.PRICE_NOT_SET;
        long maxBuyPrice = StoreConstants.PRICE_NOT_SET;
        long minSalesPrice = StoreConstants.PRICE_NOT_SET;
        long maxSalesPrice = StoreConstants.PRICE_NOT_SET;

        // initialize euro-prices to PRICE_NOT_SET
        long minBuyPriceEuro = StoreConstants.PRICE_NOT_SET;
        long minSalesPriceEuro = StoreConstants.PRICE_NOT_SET;

        // initialize currency strings
        String costCurrency = "";
        String priceCurrency = "";

        // initialize string for amounts
        str = "";

        // get the cheapest and the highest prices for consumers and merchants
        // for this product. the cheapest prices of each currency will be displayed.
        // if there is a higher price than the cheapest the token 'from <minSalesPrice>'
        // will be displayed
        minBuyPrice = this.minBuyPrice.elementAt (index).longValue ();
        maxBuyPrice = this.maxBuyPrice.elementAt (index).longValue ();
        costCurrency = this.costCurrency.elementAt (index);
        minSalesPrice = this.minSalesPrice.elementAt (index).longValue ();
        maxSalesPrice = this.maxSalesPrice.elementAt (index).longValue ();
        priceCurrency = this.priceCurrency.elementAt (index);

        // same prices in EUROAMOUNT

        minSalesPriceEuro = Helpers.getEuroAmount (priceCurrency, minSalesPrice);
/*
            maxSalesPriceEuro = Helpers.getEuroAmount (priceCurrency, maxSalesPrice);
*/
        minBuyPriceEuro = Helpers.getEuroAmount (costCurrency, minBuyPrice);
/*
            maxBuyPriceEuro = Helpers.getEuroAmount (costCurrency, maxBuyPrice);
*/

        // if there is a price set for the product
        if (minSalesPrice != StoreConstants.PRICE_NOT_SET)
        {
            // if there is a range of sale prices
            if ((maxSalesPrice != StoreConstants.PRICE_NOT_SET) &&
                (minSalesPrice < maxSalesPrice))
            {
                str = " " + 
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_FROM, env) + " ";
            } // else

            str += priceCurrency + " " +
                Helpers.moneyToString (minSalesPrice);

            // if there are prices in euro
            if (!UtilConstants.TOK_CURRENCY_EUR.equals (priceCurrency))
            {
                str += "/" + UtilConstants.TOK_CURRENCY_EUR + " " +
                    Helpers.moneyToString (minSalesPriceEuro);
            } // if

            te = new TextElement (str);

            sp = new SpanElement ();
            sp.classId = CssConstants.CLASS_LISTCONTENTSPECIAL;
            sp.addElement (te);
            ge.addElement (sp);

            str = "";

            // if there is a price set for the product
            if (minBuyPrice != StoreConstants.PRICE_NOT_SET)
            {
                // if there is a range of sale prices
                if ((maxBuyPrice != StoreConstants.PRICE_NOT_SET) &&
                    (minBuyPrice < maxBuyPrice))
                {
                    str = " " + 
                    MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                        StoreTokens.ML_FROM, env) + " ";

                } // else

                str +=    " (" + costCurrency + " " +
                    Helpers.moneyToString (minBuyPrice);

                // if there are prices in euro
                if (!UtilConstants.TOK_CURRENCY_EUR.equals (costCurrency))
                {
                    str += "/" + UtilConstants.TOK_CURRENCY_EUR + " " +
                        Helpers.moneyToString (minBuyPriceEuro);
                } // if

                str += ")";

                // create textelement for buyPrice
                te = new TextElement (str);


                sp = new SpanElement ();
                sp.classId = CssConstants.CLASS_LISTCONTENTSPECIAL;
                sp.addElement (te);
                ge.addElement (sp);
            } // if

            ge.addElement (nle);
        } // if
    } // show


    /**************************************************************************
     * Set the actual session info object. <BR/>
     *
     * @param   sess    The session info object.
     */
    void setSess (SessionInfo sess)
    {
        // set the property value:
        this.sess = sess;
    } // setSess

} // class ProductGroupElement_01





/*
 * Class: CXMLPOFilter.java
 */

// package:
package ibs.di.filter;

// imports:
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.filter.Filter;
import ibs.util.DateTimeHelpers;

import java.util.Date;
import java.util.Vector;

import org.w3c.dom.Element;


/******************************************************************************
 * The CXMLPOFilter handles all imports from XML datasources that conforms
 * to the m2 import DTD. <BR/>
 *
 * @version     $Id: CXMLPOFilter.java,v 1.11 2007/07/31 19:13:54 kreimueller Exp $
 *
 * @author      Ranjith Kumar
 ******************************************************************************
 */
public class CXMLPOFilter extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CXMLPOFilter.java,v 1.11 2007/07/31 19:13:54 kreimueller Exp $";

    /**
     * XML language attribute. <BR/>
     */
    private static final String XML_LANG = "xml:lang";

    /**
     * Format for time strings. <BR/>
     */
    private static final String FORMAT_TIME = "hh:mm:ss";


    /**************************************************************************
     * Creates an ImportFilter Object. <BR/>
     */
    public CXMLPOFilter ()
    {
        super ();
    } // CXMLPOFilter

//
// IMPORT FILTER METHODS
//

    /**************************************************************************
     * The init method reads in the import file and performs the parsing. <BR/>
     *
     * @return true initialisation succeeded or false otherwise
     */
    public boolean init ()
    {
        return true;
    } // init


    /**************************************************************************
     * Sets processing again to the beginning of all objects. <BR/>
     */
    public void reset ()
    {
        // nothing to do
    } // reset


    /**************************************************************************
     * Tests if there are more objects available from this import file. <BR/>
     *
     * @return true if there are more objects false otherwise
     */
    public boolean hasMoreObjects ()
    {
        return false;
    } // hasMoreObjects


    /**************************************************************************
     * Returns the next DataElement from this importFile. <BR/>
     *
     * @return an DataElement Object that holds the data of an object in the
     *          importFile
     */
    public DataElement nextObject ()
    {
        return null;
    } // nextObject


    /**************************************************************************
     * Returns the next DataElementList from this importFile. <BR/>
     *
     * @return an DataElementList object that holds a collection of objects
     *          the importScript can be applied on
     */
    public DataElementList nextObjectCollection ()
    {
        return null;
    } // nextObjectCollection

// EXPORT FILTER METHODS
//

    /**************************************************************************
     * It is the beginnig of the XML-document.
     * Creates the  DOM root of the XML structure and
     * store it in the insertionPoint. <BR/>
     */
    private void initExport ()
    {
        // create a new DOM root:
        // <cXML payloadID="1282000@tectum.at" timestamp="1999-03-12T18:39:09-08:00" xml:lang="ge">
        Element root = this.createDocumentRoot ("cXML");
        root.setAttribute ("payloadID", "12082000@tectum.at");
        // get the current date of the system
        Date currentDate = new Date ();
        root.setAttribute ("timestamp", this.getDateToString (currentDate , true));
        root.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
    } // initExport


    /**************************************************************************
     *Function for converting a date object to a specific format string. <BR/>
     *
     * @param   date    Date Object that has to be converted to string
     * @param   value   Shall the '-' symbol be included in the date format
     *
     * @return  The dateString which is holding
     *          the string representation of Date Object.
     */
    public String getDateToString (Date date, boolean value)
    {
        if (!value)
        {
            return DateTimeHelpers.dateTimeToString (date, "yyyyMMdd") + "T" +
                DateTimeHelpers.dateTimeToString (date, CXMLPOFilter.FORMAT_TIME);
        } // if

        return DateTimeHelpers.dateTimeToString (date, "yyyy-MM-dd") + "T" +
            DateTimeHelpers.dateTimeToString (date, CXMLPOFilter.FORMAT_TIME);
    } // getDateToString


     /**************************************************************************
     * Creates the <B>Header</B> part from the XML structure from the
     * Purchase Order Document of the CommerceOne group. <BR/>
     * It contains <B>From</B>, <B>To</B> and <B>Sender</B> details. <BR/>
     *
     * @return  the generated XML structure
     */
    private Element createHeader ()
    {
        // <Header>
        Element header = this.doc.createElement ("Header");
        // <From>
        Element from = this.doc.createElement ("From");
        header.appendChild (from);
        // <Credential domain="" type="">
        Element credential = this.doc.createElement ("Credential");
        credential.setAttribute ("domain", "");
        credential.setAttribute ("type", "");
        from.appendChild (credential);
        // <Identity></Identity>
        Element identity = this.doc.createElement ("Identity");
        identity.appendChild (this.doc.createTextNode (""));
        credential.appendChild (identity);
        // <To>
        Element to = this.doc.createElement ("To");
        header.appendChild (to);
        // <Credential domain="" type="">
        credential = this.doc.createElement ("Credential");
        credential.setAttribute ("domain", "");
        to.appendChild (credential);
        // <Identity></Identity>
        identity = this.doc.createElement ("Identity");
        identity.appendChild (this.doc.createTextNode (""));
        credential.appendChild (identity);
        // <Sender>
        Element sender = this.doc.createElement ("Sender");
        header.appendChild (sender);
        // <Credential>
        credential = this.doc.createElement ("Credential");
        credential.setAttribute ("domain", "");
        sender.appendChild (credential);
        // <Identity></Identity>
        identity = this.doc.createElement ("Identity");
        identity.appendChild (this.doc.createTextNode (""));
        credential.appendChild (identity);
        // <SharedSecret></SharedSecret>
        Element sharedSecret = this.doc.createElement ("SharedSecret");
        sharedSecret.appendChild (this.doc.createTextNode (""));
        credential.appendChild (sharedSecret);
        // <UserAgent></UserAgent>
        Element userAgent = this.doc.createElement ("UserAgent");
        userAgent.appendChild (this.doc.createTextNode (""));
        sender.appendChild (userAgent);

        return header;
    } // createHeader


    /***********************************************************************
     * Creates the <B>OrderRequestHeader</B> part from the XML structure
     * from the Purchase Order Document of the CommerceOne group. <BR/>
     * This method takes the DataElement to create the appropriate XML
     * structure. <BR/>
     * It contains <B>Total</B>, money to be paid, <B>ShipTo</B>, delivery
     * address of the item, <B>BillTo</B> payment address and <B>Tax</B>
     * details. <BR/>
     *
     * @param dataElement The Values of Order_01
     * @param totalMoney Total amount of the whole purchase
     *
     * @return the generated XML structure
     */
    private Element createOrderRequestHeader (DataElement dataElement, long totalMoney)
    {
        // <OrderRequestHeader orderId = "Order_01.voucherNo" orderDate =
        // "Order_01.voucherDate"
        // type = "new">
        Element orderRequestHeader = this.doc
            .createElement ("OrderRequestHeader");
        String voucherNo = dataElement.getImportStringValue ("voucherNo");
        Date vDate = dataElement.getImportDateTimeValue ("voucherDate");
        String voucherDate = new String ("");
        if (vDate != null)
        {
            voucherDate = this.getDateToString (vDate, false);
        } // if
        if (voucherNo != null)
        {
            orderRequestHeader.setAttribute ("orderID", voucherNo);
        } // if
        if (voucherDate != null)
        {
            orderRequestHeader.setAttribute ("orderDate", voucherDate);
        } // if
        orderRequestHeader.setAttribute ("type", "new");
        // <Total>
        Element total = this.doc.createElement ("Total");
        orderRequestHeader.appendChild (total);
        // <Money currency="OrderElement_01.priceCurrency">12.3</Money>
        Element money = this.doc.createElement ("Money");
        // get the DataElementList out of each DataElement
        DataElementList dataElementList = dataElement.dataElementList;
        // get the vector of DataElement out of DataElementList
        Vector<DataElement> dataElementVector = dataElementList.dataElements;
        String priceCurrency = dataElementVector.elementAt (0)
            .getImportStringValue ("priceCurrency");
        money.setAttribute ("currency", priceCurrency);
        money.appendChild (this.doc.createTextNode (new Long (totalMoney)
            .toString ()));
        total.appendChild (money);
        // <ShipTo>
        Element shipTo = this.doc.createElement ("ShipTo");
        orderRequestHeader.appendChild (shipTo);
        // <Address>
        Element address = this.doc.createElement ("Address");
        shipTo.appendChild (address);
        // <Name xml:lang="ge"></Name>
        Element name = this.doc.createElement ("Name");
        name.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        address.appendChild (name);
        name.appendChild (this.doc.createTextNode (""));
        // <PostalAddress name="">
        Element postalAddress = this.doc.createElement ("PostalAddress");
        postalAddress.setAttribute ("name", "");
        address.appendChild (postalAddress);
        // <DeliverTo>Order_01.deliveryName</DeliverTo>
        Element deliverTo = this.doc.createElement ("DeliverTo");
        postalAddress.appendChild (deliverTo);
        String deliveryName = dataElement.getImportStringValue ("deliveryName");
        if (deliveryName != null)
        {
            deliverTo.appendChild (this.doc.createTextNode (deliveryName));
        } // if
        // <Street>Order_01.deliveryStreet</Street>
        Element street = this.doc.createElement ("Street");
        postalAddress.appendChild (street);
        String deliveryStreet = dataElement
            .getImportStringValue ("deliveryAddress");
        if (deliveryStreet != null)
        {
            street.appendChild (this.doc.createTextNode (deliveryStreet));
        } // if
        // <City>Order_01.deliveryTown</City>
        Element city = this.doc.createElement ("City");
        postalAddress.appendChild (city);
        String deliveryTown = dataElement.getImportStringValue ("deliveryTown");
        if (deliveryTown != null)
        {
            city.appendChild (this.doc.createTextNode (deliveryTown));
        } // if
        // <State></State>
        Element state = this.doc.createElement ("State");
        postalAddress.appendChild (state);
        state.appendChild (this.doc.createTextNode (""));
        // <PostalCode>Order_01.deliveryZip</PostalCode>
        Element postalCode = this.doc.createElement ("PostalCode");
        postalAddress.appendChild (postalCode);
        String deliveryZip = dataElement.getImportStringValue ("deliveryZip");
        if (deliveryZip != null)
        {
            postalCode.appendChild (this.doc.createTextNode (deliveryZip));
        } // if
        // <Country isoCountryCode="DE">Order_01.deliveryCountry</Country>
        Element country = this.doc.createElement ("Country");
        country.setAttribute ("isoCountryCode", "DE");
        postalAddress.appendChild (country);
        String deliveryCountry = dataElement
            .getImportStringValue ("deliveryCountry");
        if (deliveryCountry != null)
        {
            country.appendChild (this.doc.createTextNode (deliveryCountry));
        } // if
        // <BillTo>
        Element billTo = this.doc.createElement ("BillTo");
        orderRequestHeader.appendChild (billTo);
        // <Address>
        address = this.doc.createElement ("Address");
        billTo.appendChild (address);
        // <Name xml:lang="ge"></Name>
        name = this.doc.createElement ("Name");
        name.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        address.appendChild (name);
        name.appendChild (this.doc.createTextNode (""));
        // <PostalAddress name="">
        postalAddress = this.doc.createElement ("PostalAddress");
        postalAddress.setAttribute ("name", "");
        address.appendChild (postalAddress);
        // <Street>Order_01.paymentAddress</Street>
        street = this.doc.createElement ("Street");
        postalAddress.appendChild (street);
        String paymentAddress = dataElement
            .getImportStringValue ("paymentAddress");
        if (paymentAddress != null)
        {
            street.appendChild (this.doc.createTextNode (paymentAddress));
        } // if
        // <City>Order_01.paymentTown</City>
        city = this.doc.createElement ("City");
        postalAddress.appendChild (city);
        String paymentTown = dataElement.getImportStringValue ("paymentTown");
        if (paymentTown != null)
        {
            city.appendChild (this.doc.createTextNode (paymentTown));
        } // if
        // <State></State>
        state = this.doc.createElement ("State");
        postalAddress.appendChild (state);
        state.appendChild (this.doc.createTextNode (""));
        // <PostalCode>Order_01.paymentZip</PostalCode>
        postalCode = this.doc.createElement ("PostalCode");
        postalAddress.appendChild (postalCode);
        String paymentZip = dataElement.getImportStringValue ("paymentZip");
        if (paymentZip != null)
        {
            postalCode.appendChild (this.doc.createTextNode (paymentZip));
        } // if
        // <Country isoCountryCode="DE">Order_01.paymentCountry</Country>
        country = this.doc.createElement ("Country");
        country.setAttribute ("isoCountryCode", "DE");
        postalAddress.appendChild (country);
        String paymentCountry = dataElement
            .getImportStringValue ("paymentCountry");
        if (paymentCountry != null)
        {
            country.appendChild (this.doc.createTextNode (paymentCountry));
        } // if
        // <Shipping trackingDomain="" trackingId="">
        Element shipping = this.doc.createElement ("Shipping");
        shipping.setAttribute ("trackingDomain", "");
        shipping.setAttribute ("trackingId", "");
        orderRequestHeader.appendChild (shipping);
        // <Money currency=""></Money>
        money = this.doc.createElement ("Money");
        money.setAttribute ("currency", "");
        shipping.appendChild (money);
        money.appendChild (this.doc.createTextNode (""));
        // <Description xml:lang="ge"></Description>
        Element description = this.doc.createElement ("Description");
        description.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        shipping.appendChild (description);
        description.appendChild (this.doc.createTextNode (""));
        // <Tax>
        Element tax = this.doc.createElement ("Tax");
        orderRequestHeader.appendChild (tax);
        // <Money currency=""></Money>
        money = this.doc.createElement ("Money");
        money.setAttribute ("currency", "");
        tax.appendChild (money);
        money.appendChild (this.doc.createTextNode (""));
        // <Description xml:lang="ge"></Description>
        description = this.doc.createElement ("Description");
        description.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        tax.appendChild (description);
        description.appendChild (this.doc.createTextNode (""));
        // <Payment>
        Element payment = this.doc.createElement ("Payment");
        orderRequestHeader.appendChild (payment);
        // <PCard number="" expiration=""/>
        Element pCard = this.doc.createElement ("PCard");
        pCard.setAttribute ("number", "");
        pCard.setAttribute ("expiration", "");
        payment.appendChild (pCard);
        // <Comments
        // xml:lang="ge-DE">Order_01.notPossible+/n+Order_01.deliveryDescription+/n
        // Order_01.deliveryDescription+/n+Order_01.shipmentDescription+\n</Comments>
        Element comments = this.doc.createElement ("Comments");
        comments.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        String notPossibleDescription = dataElement
            .getImportStringValue ("notPossibleDescription");
        String deliveryDescription = dataElement
            .getImportStringValue ("deliveryDescription");
        String shipmentDescription = dataElement
            .getImportStringValue ("shipmentDescription");
        String commentsString = new String ("");
        if (notPossibleDescription != null)
        {
            commentsString += notPossibleDescription + "\n";
        } // if
        if (deliveryDescription != null)
        {
            commentsString += deliveryDescription + "\n";
        } // if
        if (shipmentDescription != null)
        {
            commentsString += shipmentDescription + "\n";
        } // if
        comments.appendChild (this.doc.createTextNode (commentsString));
        orderRequestHeader.appendChild (comments);

        return orderRequestHeader;
    } // orderRequestHeader


    /***************************************************************************
     * Creates the <B>ItemOut</B> part from the XML structure from the Purchase
     * Order Document of the CommerceOne group. <BR/>
     * This method takes the DataElement to create the appropriate XML
     * structure. <BR/>
     * It contains <B>ItemID</B>, <B>ItemDetail</B>, <B>ShipTo</B>,<B>Shipping</B>,
     * <B>Tax</B>, <B>Distribution</B> details. <BR/>
     *
     * @param dataElement The Values of OrderElement_01
     * @param date Delivery date of the product
     *
     * @return the generated XML structure
     */
    private Element createItemOut (DataElement dataElement, String date)
    {
        // <ItemOut quantity="OrderElement_01.quantity"
        // RequestedDeliveryDate="OrderElement_01.deliveryDate">
        Element itemOut = this.doc.createElement ("ItemOut");
        String quantity = dataElement.getImportValue ("quantity");
        if (quantity != null)
        {
            itemOut.setAttribute ("quantity", quantity);
        } // if
        if (date != null)
        {
            itemOut.setAttribute ("requestedDeliveryDate", date);
        } // if
        // <ItemID>
        Element itemId = this.doc.createElement ("ItemID");
        itemOut.appendChild (itemId);
        // <SupplierPartID>OrderElement_01.productNo</SupplierPartID>
        Element supplierPartId = this.doc.createElement ("SupplierPartID");
        String productNo = dataElement.getImportValue ("productno");
        if (productNo != null)
        {
            supplierPartId.appendChild (this.doc.createTextNode (productNo));
        } // if
        itemId.appendChild (supplierPartId);
        // <ItemDetail>
        Element itemDetail = this.doc.createElement ("ItemDetail");
        itemOut.appendChild (itemDetail);
        // <UnitPrice>
        Element unitPrice = this.doc.createElement ("UnitPrice");
        itemDetail.appendChild (unitPrice);
        // <Money
        // currency="OrderElement_01.currency">OrderElement_01.totalPrice</Money>
        Element money = this.doc.createElement ("Money");
        String priceCurrency = dataElement
            .getImportStringValue ("priceCurrency");
        if (priceCurrency != null)
        {
            money.setAttribute ("currency", priceCurrency);
        } // if
        String totalPrice = dataElement.getImportValue ("totalPrice");
        if (totalPrice != null)
        {
            money.appendChild (this.doc.createTextNode (totalPrice));
        } // if
        unitPrice.appendChild (money);
        // <Description
        // xml:lang="ge">OrderElement_01.description</Description>
        Element description = this.doc.createElement ("Description");
        description.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        String descriptionString = dataElement
            .getImportStringValue ("productDescription");
        if (descriptionString != null)
        {
            description.appendChild (this.doc
                .createTextNode (descriptionString));
        } // if
        itemDetail.appendChild (description);
        // <UnitOfMeasure>OrderElement_01.unitOfQty</UnitOfMeasure>
        Element unitOfMeasure = this.doc.createElement ("UnitOfMeasure");
        String unitOfQty = dataElement.getImportValue ("unitOfQty");
        if (unitOfQty != null)
        {
            unitOfMeasure.appendChild (this.doc.createTextNode (unitOfQty));
        } // if
        itemDetail.appendChild (unitOfMeasure);
        // <Classification
        // domain=""></Classification>
        Element classification = this.doc.createElement ("Classification");
        classification.setAttribute ("domain", "");
        classification.appendChild (this.doc.createTextNode (""));
        itemDetail.appendChild (classification);
        // <ManufacturerPartID></ManufacturerPartID>
        Element manufacturerPartId = this.doc
            .createElement ("ManufacturerPartID");
        manufacturerPartId.appendChild (this.doc.createTextNode (""));
        itemDetail.appendChild (manufacturerPartId);
        // <ManufacturerPartName></ManufacturerPartName>
        Element manufacturerName = this.doc
            .createElement ("ManufacturerPartName");
        manufacturerName.appendChild (this.doc.createTextNode (""));
        itemDetail.appendChild (manufacturerName);
        // <URL></URL>
        Element url = this.doc.createElement ("URL");
        url.appendChild (this.doc.createTextNode (""));
        itemDetail.appendChild (url);
        // <ShipTo>
        Element shipTo = this.doc.createElement ("ShipTo");
        itemOut.appendChild (shipTo);
        // <Address>
        Element address = this.doc.createElement ("Address");
        shipTo.appendChild (address);
        // <Name
        // xml:lang=""></Name>
        Element name = this.doc.createElement ("Name");
        name.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        name.appendChild (this.doc.createTextNode (""));
        address.appendChild (name);
        // <PostalAddress
        // name="">
        Element postalAddress = this.doc.createElement ("PostalAddress");
        postalAddress.setAttribute ("name", "");
        address.appendChild (postalAddress);
        // <Street></Street>
        Element street = this.doc.createElement ("Street");
        street.appendChild (this.doc.createTextNode (""));
        postalAddress.appendChild (street);
        // <City></City>
        Element city = this.doc.createElement ("City");
        city.appendChild (this.doc.createTextNode (""));
        postalAddress.appendChild (city);
        // <State></State>
        Element state = this.doc.createElement ("State");
        state.appendChild (this.doc.createTextNode (""));
        postalAddress.appendChild (state);
        // <PostalCode></PostalCode>
        Element postalCode = this.doc.createElement ("PostalCode");
        postalCode.appendChild (this.doc.createTextNode (""));
        postalAddress.appendChild (postalCode);
        // <Country
        // isoCountryCode
        // =
        // ""></Country>
        Element country = this.doc.createElement ("Country");
        country.setAttribute ("isoCountryCode", "");
        country.appendChild (this.doc.createTextNode (""));
        postalAddress.appendChild (country);
        // <Shipping>
        Element shipping = this.doc.createElement ("Shipping");
        itemOut.appendChild (shipping);
        // <Money
        // currency=""></Money>
        money = this.doc.createElement ("Money");
        money.setAttribute ("currency", "");
        money.appendChild (this.doc.createTextNode (""));
        shipping.appendChild (money);
        // <Description xml:lang=""></Description>
        description = this.doc.createElement ("Description");
        description.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        description.appendChild (this.doc.createTextNode (""));
        shipping.appendChild (description);
        // <Tax>
        Element tax = this.doc.createElement ("Tax");
        itemOut.appendChild (tax);
        // <Money currency=""></Money>
        money = this.doc.createElement ("Money");
        money.setAttribute ("currency", "");
        money.appendChild (this.doc.createTextNode (""));
        tax.appendChild (money);
        // <Description xml:lang=""></Description>
        description = this.doc.createElement ("Description");
        description.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        description.appendChild (this.doc.createTextNode (""));
        tax.appendChild (description);
        // <Distribution>
        Element distribution = this.doc.createElement ("Distribution");
        itemOut.appendChild (distribution);
        // <Accounting name="">
        Element accounting = this.doc.createElement ("Accounting");
        accounting.setAttribute ("name", "");
        distribution.appendChild (accounting);
        // <Segment type="" id="" description=""/>
        Element segment = this.doc.createElement ("Segment");
        segment.setAttribute ("type", "");
        segment.setAttribute ("id", "");
        segment.setAttribute ("description", "");
        accounting.appendChild (segment);
        // <Segment type="" id="" description=""/>
        segment = this.doc.createElement ("Segment");
        segment.setAttribute ("type", "");
        segment.setAttribute ("id", "");
        segment.setAttribute ("description", "");
        accounting.appendChild (segment);
        // <Charge>
        Element charge = this.doc.createElement ("Charge");
        distribution.appendChild (charge);
        // <Money currency=""></Money>
        money = this.doc.createElement ("Money");
        money.setAttribute ("currency", "");
        money.appendChild (this.doc.createTextNode (""));
        charge.appendChild (money);
        // <Comments xml:lang="ge-DE"></Comments>
        Element comments = this.doc.createElement ("Comments");
        comments.setAttribute (CXMLPOFilter.XML_LANG, DIConstants.XML_LANG);
        comments.appendChild (this.doc.createTextNode (""));
        itemOut.appendChild (comments);

        return itemOut;
    } // createItemOut


    /**************************************************************************
     * Creates an XML document out of an array of dataElements.
     * This is the method that must be implemented in the subclasses. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean create (DataElement [] dataElements)
    {
        // return
        return false;
    } // create


    /**************************************************************************
     * Creates an XML document. <BR/>
     *
     * @param dataElements  a dataElement to create the export document from
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean create (DataElement dataElements)
    {
        // check if we got an DataElement
        if (dataElements == null)
        {
            return false;
        } // if
        // init the export document
        this.initExport ();
        // add the dataElements
        return this.add (dataElements);
    } // create


    /**************************************************************************
     * Sums up the totalPrice of all items of dataElements in dataElementList. <BR/>
     *
     * @param   dataElements    Vector which contains DataElements.
     *
     * @return  Total price of the items in a dataElement.
     */
    private long getTotalMoney (DataElement dataElements)
    {
        long total = 0;

        // get the dataElement vector out of dataElementList.
        Vector<DataElement> dataElementVector = dataElements.dataElementList.dataElements;
        for (int j = 0; j < dataElementVector.size (); j++)
        {
            total += dataElementVector.elementAt (j)
                .getImportDoubleValue ("totalPrice");
        } // for j
        return total;
    } // getTotalMoney


    /**************************************************************************
     * Adds a set of object definitions to the export document.
     * In case there is no export document created already it will be
     * initialized first. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean add (DataElement [] dataElements)
    {
        // return
        return false;
    } // add


    /**************************************************************************
     * Adds a set of object definitions to an export document.
     * In case there is no export document created already it will be
     * initialized first. <BR/>
     * The method uses the insertionPoint property in order to determine
     * where to add the object definitions contained in the dataElement array.
     * <BR/>
     *
     * @param dataElements  a dataElement to add to the export document
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean add (DataElement dataElements)
    {
        long total = 0;     // holds the toal price of the items in a single DataElement
        // check if we got a DataElement
        if (dataElements == null)
        {
            return false;
        } // if
        // check if the export file has been initialized
        if (this.doc == null)
        {
            this.initExport ();
        } // if
        // create the header area this will come only one time per document
        this.p_insertionPoint.appendChild (this.createHeader ());
        // <Request deploymentMode="">
        Element request = this.doc.createElement ("Request");
        request.setAttribute ("deploymentMode", "test");
        this.p_insertionPoint.appendChild (request);
        // <orderRequest>
        Element orderRequest = this.doc.createElement ("OrderRequest");
        request.appendChild (orderRequest);
        // get the total cost of all the items
        total = this.getTotalMoney (dataElements);
        orderRequest.appendChild (this.createOrderRequestHeader (dataElements, total));
        // get the DataElementList out of each DataElement
        DataElementList dataElementList =  dataElements .dataElementList;
        // get the vector of DataElement out of DataElementList
        Vector<DataElement> dataElementVector = dataElementList.dataElements;
        // now loop through dataElements
        for (int i = 0; i < dataElementVector.size (); i++)
        {
            Date dateObject = dataElements .getImportDateTimeValue ("deliveryDate");
            String date = new String ("");
            if (dateObject != null)
            {
                date = this.getDateToString (dateObject, false);
            } // if
            orderRequest.appendChild (this.createItemOut (
                dataElementVector.elementAt (i), date));
        } // for
        // true to indicate that everything is ok
        return true;
    } // add

} // class CXMLPOFilter

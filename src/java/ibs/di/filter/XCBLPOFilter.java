/*
 * Class: XCBLPOFilter.java
 */

// package:
package ibs.di.filter;

// imports:
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.filter.Filter;
import ibs.util.DateTimeHelpers;

import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Element;


/******************************************************************************
 * The XCBLFilter handles all imports from XML datasources that conforms
 * to the m2 import DTD. <BR/>
 *
 * @version     $Id: XCBLPOFilter.java,v 1.10 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Danny Xavier (DX), 000809
 ******************************************************************************
 */
public class XCBLPOFilter extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XCBLPOFilter.java,v 1.10 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * counter for counting the total no of lines
     */
    public int counter = 1;

    /**
     * variable for counting the total price
     */
    public float totalPrice = 0;

    /**
     * XML document element: reference. <BR/>
     */
    private static final String ELEM_REFERENCE = "Reference";
    /**
     * XML document element: reference number. <BR/>
     */
    private static final String ELEM_REFERENCENUMBER = "RefNum";
    /**
     * XML document element: party. <BR/>
     */
    private static final String ELEM_PARTY = "Party";
    /**
     * XML document element: name address. <BR/>
     */
    private static final String ELEM_NAMEADDRESS = "NameAddress";
    /**
     * XML document element: name 1. <BR/>
     */
    private static final String ELEM_NAME1 = "Name1";
    /**
     * XML document element: name 2. <BR/>
     */
    private static final String ELEM_NAME2 = "Name2";
    /**
     * XML document element: address 1. <BR/>
     */
    private static final String ELEM_ADDRESS1 = "Address1";
    /**
     * XML document element: address 2. <BR/>
     */
    private static final String ELEM_ADDRESS2 = "Address2";
    /**
     * XML document element: city. <BR/>
     */
    private static final String ELEM_CITY = "City";
    /**
     * XML document element: state. <BR/>
     */
    private static final String ELEM_STATE = "StateOrProvince";
    /**
     * XML document element: postal code. <BR/>
     */
    private static final String ELEM_ZIP = "PostalCode";
    /**
     * XML document element: postal code. <BR/>
     */
    private static final String ELEM_COUNTRY = "Country";

    /**
     * Field name: description. <BR/>
     */
    private static final String FIELD_DESCRIPTION = "description";


    /**************************************************************************
     * Creates a XCBLPOFilter Object. <BR/>
     */
    public XCBLPOFilter ()
    {
        super ();
    } // XCBLPOFilter

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


    //
    // EXPORT FILTER METHODS
    //
    /**************************************************************************
     * It`s the beginnig of the XML-document.
     * Creates the  DOM root of the XML structure and
     * store the insertionPoint. <BR/>
     */
    private void initExport ()
    {
        // create a new DOM root:
        this.createDocumentRoot ("PurchaseOrder");
    } // initExport


    /**************************************************************************
     * Creates the OrderHeader from the XML-Structure from the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param dataElement  with all Values from DataElement
     *
     * @return  the generated XML structure
     */
    private Element createOrderHeader (DataElement dataElement)
    {
        String str;
        Date date;
        //<OrderHeader>
        Element  orderHeader = this.doc.createElement ("OrderHeader");
        //<pOIssuedDate>
        Element  pOIssuedDate = this.doc.createElement ("POIssuedDate");
        date = dataElement.getImportDateTimeValue ("voucherDate");
        str = this.dateToString (date);
        if (str != null)
        {
            pOIssuedDate.appendChild (this.doc.createTextNode (str));
        } // if
        orderHeader.appendChild (pOIssuedDate);
        //<RequestedDeliveryDate>
        Element requestedDeliveryDate = this.doc.createElement ("RequestedDeliveryDate");
        date = dataElement.getImportDateTimeValue ("deliveryDate");
        str = this.dateToString (date);
        if (str != null)
        {
            requestedDeliveryDate.appendChild (this.doc.createTextNode (str));
        } // if
        orderHeader.appendChild (requestedDeliveryDate);
        //<ShipByDate>
        Element shipByDate = this.doc.createElement ("ShipByDate");
        shipByDate.appendChild (this.doc.createTextNode (""));
        orderHeader.appendChild (shipByDate);
        //<OrderReference>
        Element orderReference = this.createOrderReference (dataElement);
        orderHeader.appendChild (orderReference);
        //<OrderParty>
        Element orderParty = this.createOrderParty (dataElement);
        orderHeader.appendChild (orderParty);
        //<Tax>
        Element  tax = this.createTaxDetails ();
        orderHeader.appendChild (tax);
        //<OrderCurrency>
        Element  orderCurrency = this.doc.createElement ("OrderCurrency");
        orderCurrency.appendChild (this.doc.createTextNode (""));
        orderHeader.appendChild (orderCurrency);
        //<OrderLanguage>
        Element  orderLanguage = this.doc.createElement ("OrderLanguage");
        orderLanguage.appendChild (this.doc.createTextNode ("GE"));
        orderHeader.appendChild (orderLanguage);
        //<Payment>
        Element  payment = this.createPaymentDetails ();
        orderHeader.appendChild (payment);
        //<PartialShipmentAllowed>
        Element  partialShipmentAllowed = this.doc.createElement ("PartialShipmentAllowed");
        partialShipmentAllowed.appendChild (this.doc.createTextNode (""));
        orderHeader.appendChild (partialShipmentAllowed);
        //<SpecialHandlingNote>
        Element specialHandlingNote  = this.doc.createElement ("SpecialHandlingNote");
        specialHandlingNote.appendChild (this.doc.createTextNode (""));
        orderHeader.appendChild (specialHandlingNote);
        //<GeneralNote>
        Element generalNote  = this.doc.createElement ("GeneralNote");
        str = dataElement.getImportValue (XCBLPOFilter.FIELD_DESCRIPTION);
        if (str != null)
        {
            generalNote.appendChild (this.doc.createTextNode (str));
        } // if
        orderHeader.appendChild (generalNote);
        //<PartLocation></PartLocation>
        Element partLocation = this.doc.createElement ("PartLocation");
        partLocation.appendChild (this.doc.createTextNode (""));
        orderHeader.appendChild (partLocation);
        str = dataElement.getImportValue ("shipmentDescription");
        Element transport = this.createTransportInfo (str);
        orderHeader.appendChild (transport);
        Element termOfDelivery  = this.createTermOfDelivery ();
        orderHeader.appendChild (termOfDelivery);
        Element orderHeaderAttachment  =  this.doc.createElement ("OrderHeaderAttachment");
        Element listOfAttachment  =  this.doc.createElement ("ListOfAttachment");
        listOfAttachment.appendChild (this.doc.createTextNode (""));
        orderHeaderAttachment.appendChild (listOfAttachment);
        orderHeader.appendChild (orderHeaderAttachment);
        return orderHeader;
    } // createOrderHeader


    /**************************************************************************
     * Creates the OrderReference from the XML-Structure from the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param dataElement  with all Values from DataElement
     *
     * @return  the generated XML structure
     */
    private Element createOrderReference (DataElement dataElement)
    {
        String str;
        //<OrderReference>
        Element orderReference = this.doc.createElement ("OrderReference");
        //<AccountCode>
        Element accountCode = this.doc.createElement ("AccountCode");
        //<Reference>
        Element reference = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCE);
        //<RefNum>
        Element refNum = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCENUMBER);
        refNum.appendChild (this.doc.createTextNode (""));
        reference.appendChild (refNum);
        accountCode.appendChild (reference);
        //<BuyerRefNum>
        Element buyerRefNum = this.doc.createElement ("BuyerRefNum");
        //<Reference>
        Element buyReference = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCE);
        //<RefNum>
        Element buyRefNum = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCENUMBER);
        str = dataElement.getImportValue ("voucherNo");
        if (str != null)
        {
            buyRefNum.appendChild (this.doc.createTextNode (str));
        } // if
        buyReference.appendChild (buyRefNum);
        buyerRefNum.appendChild (buyReference);
        //<SupplierRefNum>
        Element  supplierRefNum = this.doc.createElement ("SupplierRefNum");
        //<Reference>
        Element  supReference = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCE);
        //<RefNum>
        Element  supRefNum = this.doc.createElement (XCBLPOFilter.ELEM_REFERENCENUMBER);
        supRefNum.appendChild (this.doc.createTextNode (""));
        supReference.appendChild (supRefNum);
        supplierRefNum.appendChild (supReference);
        orderReference.appendChild (supplierRefNum);
        orderReference.appendChild (buyerRefNum);
        orderReference.appendChild (accountCode);
        //return orderReference
        return orderReference;
    } // createOrderReference


    /**************************************************************************
     * Creates the OrderParty area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param dataElement with the Values of Order_01
     *
     * @return  the generated XML structure
     */
    private Element createOrderParty (DataElement dataElement)
    {
        //<OrderParty>
        Element orderParty = this.doc.createElement ("OrderParty");
        //<BuyerParty>
        orderParty.appendChild (this.createBuyerParty (dataElement));
        //<SupplierParty>
        orderParty.appendChild (this.createSupplierParty (dataElement));
        return orderParty;
    } // createOrderParty


    /**************************************************************************
     * Creates the BuyerParty area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param dataElement with the Values of Order_01
     *
     * @return  the generated XML structure
     */
    private Element createBuyerParty (DataElement dataElement)
    {
        String str;
        // <buyerParty>
        Element buyerParty = this.doc.createElement ("BuyerParty");
        // <Party>
        Element party = this.doc.createElement (XCBLPOFilter.ELEM_PARTY);
        // <NameAddress>
        Element nameAddress = this.doc.createElement (XCBLPOFilter.ELEM_NAMEADDRESS);
        //<Name1>
        Element name1 = this.doc.createElement (XCBLPOFilter.ELEM_NAME1);
        str = dataElement.getImportValue ("deliveryName");
        if (str != null)
        {
            name1.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (name1);
        //<Name2>
        Element    name2 = this.doc.createElement (XCBLPOFilter.ELEM_NAME2);
        name2.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (name2);
        //Address1
        Element    address1 = this.doc.createElement (XCBLPOFilter.ELEM_ADDRESS1);
        str = dataElement.getImportValue ("deliveryAddress");
        if (str != null)
        {
            address1.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (address1);
        //<Address2>
        Element    address2 = this.doc.createElement (XCBLPOFilter.ELEM_ADDRESS2);
        address2.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (address2);
        //<City>
        Element    city = this.doc.createElement (XCBLPOFilter.ELEM_CITY);
        str = dataElement.getImportValue ("deliveryTown");
        if (str != null)
        {
            city.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (city);
        //<StateOrProvince>
        Element    stateOrProvince = this.doc.createElement (XCBLPOFilter.ELEM_STATE);
        stateOrProvince.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (stateOrProvince);
        //<PostalCode>
        Element    postalCode = this.doc.createElement (XCBLPOFilter.ELEM_ZIP);
        str = dataElement.getImportValue ("deliveryZip");
        if (str != null)
        {
            postalCode.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (postalCode);
        //<Country>
        Element    country = this.doc.createElement (XCBLPOFilter.ELEM_COUNTRY);
        str = dataElement.getImportValue ("deliveryCountry");
        if (str != null)
        {
            country.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (country);
        party.appendChild (nameAddress);
        //<OrderContact>
        Element orderContact = this.doc.createElement ("OrderContact");
        //<contact>
        Element contact = this.createContactDetails ();
        orderContact.appendChild (contact);
        party.appendChild (orderContact);
        //<ReceivingContact>
        Element    receivingContact = this.doc.createElement ("ReceivingContact");
        //<Contact>
        Element    recvContact = this.createContactDetails ();
        receivingContact.appendChild (recvContact);
        party.appendChild (receivingContact);
        //<ShippingContact>
        Element    shippingContact = this.doc.createElement ("ShippingContact");
        //<Contact>
        Element    shipContact = this.createContactDetails ();
        shippingContact.appendChild (shipContact);
        party.appendChild (shippingContact);
        buyerParty.appendChild (party);
        return buyerParty;
    } // createOrderParty


    /**************************************************************************
     * Creates the SupplierParty area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param dataElement with the Values of Order_01
     *
     * @return  the generated XML structure
     */
    private Element createSupplierParty (DataElement dataElement)
    {
        String str;
        //<SupplierParty>
        Element supplierParty = this.doc.createElement ("SupplierParty");
        //<Party>
        Element party = this.doc.createElement (XCBLPOFilter.ELEM_PARTY);
        //<NameAddress>
        Element nameAddress = this.doc.createElement (XCBLPOFilter.ELEM_NAMEADDRESS);
        //<Name1>
        Element name1 = this.doc.createElement (XCBLPOFilter.ELEM_NAME1);
        str = dataElement.getImportValue ("supplier");
        if (str != null)
        {
            name1.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (name1);
        //<Name2>
        Element name2 = this.doc.createElement (XCBLPOFilter.ELEM_NAME2);
        str = dataElement.getImportValue ("supplierCompany");
        if (str != null)
        {
            name2.appendChild (this.doc.createTextNode (str));
        } // if
        nameAddress.appendChild (name2);
        //<Address1>
        Element address1 = this.doc.createElement (XCBLPOFilter.ELEM_ADDRESS1);
        address1.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (address1);
        //<Address2>
        Element address2 = this.doc.createElement (XCBLPOFilter.ELEM_ADDRESS2);
        address2.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (address2);
        //<City>
        Element city = this.doc.createElement (XCBLPOFilter.ELEM_CITY);
        city.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (city);
        //<StateOrProvince>
        Element stateOrProvince = this.doc.createElement (XCBLPOFilter.ELEM_STATE);
        stateOrProvince.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (stateOrProvince);
        //<PostalCode>
        Element postalCode = this.doc.createElement (XCBLPOFilter.ELEM_ZIP);
        postalCode.appendChild (this.doc.createTextNode (""));
        nameAddress.appendChild (postalCode);
        //<Country>
        Element country = this.doc.createElement (XCBLPOFilter.ELEM_COUNTRY);
        country.appendChild (this.doc.createTextNode (""));
        // append country to the address
        nameAddress.appendChild (country);
        // append address to the party
        party.appendChild (nameAddress);
        //append party to the supplier party
        supplierParty.appendChild (party);
        return supplierParty;
    } // createSupplierParty


    /**************************************************************************
     * Creates an XML document. <BR/>
     *
     * @param dataElements  a dataElement array to create the export document
     *                      from
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean create (DataElement[] dataElements)
    {
        return false;
    } // create


    /**************************************************************************
     * Creates an XML document. <BR/>
     *
     * @param dataElements  a dataElement to create the export document
     *                      from
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
     * Adds a set of object definitions to an export document.
     * In case there is no export document created already it will be
     * initialized first. It  make the OrderHeader ,OrderDetail
     * and OrderSummary takes it  to the indicationPoint(PurchaseOrder).
     *
     * The method uses the insertionPoint property in order to determine
     * where to add the object definitions contained in the dataElement array.
     * <BR/>
     *
     * @param   dataElement A DataElement array containing all elements to be
     *                      added to the export document.
     *
     * @return  <CODE>true</CODE> if the export document has been created
     *          successfully or <CODE>false</CODE> otherwise.
     */
    public boolean add (DataElement[] dataElement)
    {
        return false;
    } // add


    /**************************************************************************
     * Adds a set of object definitions to an export document.
     * In case there is no export document created already it will be
     * initialized first. It  make the OrderHeader ,OrderDetail
     * and OrderSummary takes it  to the indicationPoint(PurchaseOrder).
     *
     * The method uses the insertionPoint property in order to determine
     * where to add the object definitions contained in the dataElement array.
     * <BR/>
     *
     * @param dataElement    a dataElement to add to the export document
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean add (DataElement dataElement)
    {
        // check if we got a DataElement
        if (dataElement == null)
        {
            return false;
        } // if
        // check if the export file has been initialized
        if (this.doc == null)
        {
            this.initExport ();
        } // if
        // now loop through the dataElements vector
        // create the Order Header
        this.p_insertionPoint.appendChild (this.createOrderHeader (dataElement));
        Element listOfOrderDetail = this.doc.createElement ("ListOfOrderDetail");
        listOfOrderDetail.appendChild (this.getOrderDetail (dataElement));
        this.p_insertionPoint.appendChild (listOfOrderDetail);
        this.p_insertionPoint.appendChild (this.createOrderSummary ());
        // true to indicate that everything is ok*/
        return true;
    } // add


    /**************************************************************************
     * Creates the ContactDetails area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @return  the generated XML structure
     */
    private Element createContactDetails ()
    {
        //<contact>
        Element    contact = this.doc.createElement ("Contact");
        //<contactName>
        Element    contactName = this.doc.createElement ("ContactName");
        contactName.appendChild (this.doc.createTextNode (""));
        contact.appendChild (contactName);
        //<Telephone>
        Element    telephone = this.doc.createElement ("Telephone");
        telephone.appendChild (this.doc.createTextNode (""));
        contact.appendChild (telephone);
        //<Email>
        Element    email = this.doc.createElement ("Email");
        email.appendChild (this.doc.createTextNode (""));
        contact.appendChild (email);
        //<Fax>
        Element    fax = this.doc.createElement ("Fax");
        fax.appendChild (this.doc.createTextNode (""));
        contact.appendChild (fax);
        return contact;
    } // createContactDetails


    /**************************************************************************
     * Creates the Tax area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @return  the generated XML structure
     */
    private Element createTaxDetails ()
    {
        //<Tax>
        Element    tax = this.doc.createElement ("Tax");
        //<TaxPercent></TaxPercent>
        Element taxPercent = this.doc.createElement ("TaxPercent");
        taxPercent.appendChild (this.doc.createTextNode (""));
        tax.appendChild (taxPercent);
        //<Location></Location>
        Element location = this.doc.createElement ("Location");
        location.appendChild (this.doc.createTextNode (""));
        tax.appendChild (location);
        //<TaxId></TaxId>
        Element taxId = this.doc.createElement ("TaxId");
        taxId.appendChild (this.doc.createTextNode (""));
        tax.appendChild (taxId);
        //<TaxAmount></TaxAmount>
        Element taxAmount = this.doc.createElement ("TaxAmount");
        taxAmount.appendChild (this.doc.createTextNode (""));
        tax.appendChild (taxAmount);
        //<TaxableAmount></TaxableAmount
        Element taxableAmount = this.doc.createElement ("TaxableAmount");
        taxableAmount.appendChild (this.doc.createTextNode (""));
        tax.appendChild (taxableAmount);
        return tax;
    } // createTaxDetails


    /**************************************************************************
     * Creates the PaymentDetails area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @return  the generated XML structure
     */
    private  Element createPaymentDetails ()
    {
        //<Payment>
        Element payment = this.doc.createElement ("Payment");
        //PaymentMean></PaymentMean>
        Element paymentMean = this.doc.createElement ("PaymentMean");
        paymentMean.appendChild (this.doc.createTextNode (""));
        payment.appendChild (paymentMean);
        //<PaymentTerm></PaymentTerm>
        Element paymentTerm = this.doc.createElement ("PaymentTerm");
        paymentTerm.appendChild (this.doc.createTextNode (""));
        payment.appendChild (paymentTerm);
        //<DiscountPercent></DiscountPercent>
        Element discountPercent = this.doc.createElement ("DiscountPercent");
        discountPercent.appendChild (this.doc.createTextNode (""));
        payment.appendChild (discountPercent);
        //<DiscountDaysDue></DiscountDaysDue>
        Element discountDaysDue = this.doc.createElement ("DiscountDaysDue");
        discountDaysDue.appendChild (this.doc.createTextNode (""));
        payment.appendChild (discountDaysDue);
        //<DiscountTimeRef></DiscountTimeRef>
        Element discountTimeRef = this.doc.createElement ("DiscountTimeRef");
        discountTimeRef.appendChild (this.doc.createTextNode (""));
        payment.appendChild (discountTimeRef);
        //<NetDaysDue></NetDaysDue>
        Element netDaysDue = this.doc.createElement ("NetDaysDue");
        netDaysDue.appendChild (this.doc.createTextNode (""));
        payment.appendChild (netDaysDue);
        //<NetTimeRef></NetTimeRef>
        Element netTimeRef = this.doc.createElement ("NetTimeRef");
        netTimeRef.appendChild (this.doc.createTextNode (""));
        payment.appendChild (netTimeRef);
        //<CardInfo>
        Element cardInfo = this.doc.createElement ("CardInfo");
        //<CardNum></CardNum>
        Element cardNum = this.doc.createElement ("CardNum");
        cardNum.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardNum);
        //<CardAuthCode></CardAuthCode>
        Element cardAuthCode = this.doc.createElement ("CardAuthCode");
        cardAuthCode.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardAuthCode);
        //<CardRefNum></CardRefNum>
        Element cardRefNum = this.doc.createElement ("CardRefNum");
        cardRefNum.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardRefNum);
        //<CardExpirationDate></CardExpirationDate>
        Element cardExpirationDate = this.doc.createElement ("CardExpirationDate");
        cardExpirationDate.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardExpirationDate);
        //<CardType></CardType>
        Element cardType = this.doc.createElement ("CardType");
        cardType.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardType);
        //<CardHolderName></CardHolderName>
        Element cardHolderName = this.doc.createElement ("CardHolderName");
        cardHolderName.appendChild (this.doc.createTextNode (""));
        cardInfo.appendChild (cardHolderName);
        payment.appendChild (cardInfo);
        return payment;
    } // createPaymentDetails


    /**************************************************************************
     * Creates the TransportInfo area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param   string  ???
     *
     * @return  the generated XML structure
     */
    private Element createTransportInfo (String string)
    {
        String str;
        Element transport  = this.doc.createElement ("Transport");
        transport.setAttribute ("Direction", "SupplierToBuyer");
        //<Mode></Mode>
        Element mode  = this.doc.createElement ("Mode");
        mode.appendChild (this.doc.createTextNode (""));
        transport.appendChild (mode);
        //<Mean></Mean>
        Element mean  = this.doc.createElement ("Mean");
        mean.appendChild (this.doc.createTextNode (""));
        transport.appendChild (mean);
        //<Carrier></Carrier>
        Element carrier  = this.doc.createElement ("Carrier");
        carrier.appendChild (this.doc.createTextNode (""));
        transport.appendChild (carrier);
        //<CustShippingContractNum></CustShippingContractNum>
        Element custShippingContractNum  = this.doc.createElement ("CustShippingContractNum");
        custShippingContractNum.appendChild (this.doc.createTextNode (""));
        transport.appendChild (custShippingContractNum);
        //<ShippingInstruction>
        Element shippingInstruction = this.doc.createElement ("ShippingInstruction");
        str = string;
        shippingInstruction.appendChild (this.doc.createTextNode (str));
        transport.appendChild (shippingInstruction);
        return transport;
    } // createTransPortInfo


    /**************************************************************************
     * Creates the TermOfDelivery area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @return  the generated XML structure
     */
    private Element createTermOfDelivery ()
    {
        //<TermOfDelivery TODFunction_a="Delivery">
        Element termOfDelivery = this.doc.createElement ("TermOfDelivery");
        termOfDelivery.setAttribute ("TODFunction_a", "Delivery");
        //<Code></Code>
        Element code = this.doc.createElement ("Code");
        code.appendChild (this.doc.createTextNode (""));
        termOfDelivery.appendChild (code);
        //<FOBCity></FOBCity>
        Element fOBCity = this.doc.createElement ("FOBCity");
        fOBCity.appendChild (this.doc.createTextNode (""));
        termOfDelivery.appendChild (fOBCity);
        //<FOBLocation></FOBLocation>
        Element fOBLocation = this.doc.createElement ("FOBLocation");
        fOBLocation .appendChild (this.doc.createTextNode (""));
        termOfDelivery.appendChild (fOBLocation);
        //<FOBInstruction></FOBInstruction>
        Element fOBInstruction = this.doc.createElement ("FOBInstruction");
        fOBInstruction.appendChild (this.doc.createTextNode (""));
        termOfDelivery.appendChild (fOBInstruction);
        //<ShippingPaymentMethod></ShippingPaymentMethod>
        Element shippingPaymentMethod = this.doc.createElement ("ShippingPaymentMethod");
        shippingPaymentMethod.appendChild (this.doc.createTextNode (""));
        termOfDelivery.appendChild (shippingPaymentMethod);
        return termOfDelivery;
    } // createTermOfDelivery


    /**************************************************************************
     * Creates the OrderDetail area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @param   dataElement ???
     *
     * @return  the generated XML structure
     */
    private Element getOrderDetail (DataElement dataElement)
    {
        String str;
        Element orderDetail  = this.doc.createElement ("OrderDetail");
        Element baseItemDetail  = this.doc.createElement ("BaseItemDetail");
        // orderDetail.appendChild (baseItemDetail);
        DataElement lineDataElement = null;

        // takes all elements out of the dataElementList:
        // create a counter for the LINENUM in the ITEMLINE
        // get attributes from the DataElement to use them in the method ceateItemLine
        for (Iterator<DataElement> iter =
                dataElement.dataElementList.dataElements.iterator ();
             iter.hasNext ();)
        {
            // get a dataElement that represents an object
            lineDataElement = iter.next ();
            // create an itemLine out of the DataElement
            Element lineItemNum  = this.doc.createElement ("LineItemNum");
            lineItemNum.appendChild (this.doc.createTextNode (Integer
                .toString (this.counter)));
            baseItemDetail.appendChild (lineItemNum);
            Element supplierPartNum = this.doc.createElement ("SupplierPartNum");
            // <PartNum>
            Element partNum = this.doc.createElement ("PartNum");
            // <Agency AgencyID=""/>
            Element agency = this.doc.createElement ("Agency");
            agency.setAttribute ("AgencyID", "");
            partNum.appendChild (agency);
            // <PartID>OrderElement_01. productno</PartID>
            Element partID = this.doc.createElement ("PartID");
            str = lineDataElement.getImportValue ("productno");
            if (str != null)
            {
                partID.appendChild (this.doc.createTextNode (str));
            } // if
            partNum.appendChild (partID);
            supplierPartNum.appendChild (partNum);
            baseItemDetail.appendChild (supplierPartNum);
            // <ItemDescription>OrderElement_01.description</ItemDescription>
            // get the
            Element itemDescription = this.doc.createElement ("ItemDescription");
            // get the value of itemDescription
            str = lineDataElement.getImportValue (XCBLPOFilter.FIELD_DESCRIPTION);
            if (str != null)
            {
                itemDescription.appendChild (this.doc.createTextNode (str));
            } // if
            baseItemDetail.appendChild (itemDescription);
            // <Quantity>
            Element quantity = this.doc.createElement ("Quantity");
            // <Qty>OrderElement_01.quantity</Qty>
            Element qty = this.doc.createElement ("Qty");
            str = lineDataElement.getImportValue ("quantity");
            if (str != null)
            {
                qty.appendChild (this.doc.createTextNode (str));
            } // if
            quantity.appendChild (qty);
            // <UnitOfMeasure>
            Element unitOfMeasure = this.doc.createElement ("UnitOfMeasure");
            // <UOM>OrderElement_01. packingUnit</UOM>
            Element uom = this.doc.createElement ("UOM");
            // get the value of UOM
            str = lineDataElement.getImportValue ("packingUnit");
            if (str != null)
            {
                uom.appendChild (this.doc.createTextNode (str));
            } // if
            unitOfMeasure.appendChild (uom);
            quantity.appendChild (unitOfMeasure);
            baseItemDetail.appendChild (quantity);
            // <Transport Direction="SupplierToBuyer">
            Element transport = this.createTransportInfo ("");
            baseItemDetail.appendChild (transport);
            // <OffCatalogFlag></OffCatalogFlag>
            Element offCatalogFlag = this.doc.createElement ("OffCatalogFlag");
            offCatalogFlag.appendChild (this.doc.createTextNode (""));
            baseItemDetail.appendChild (offCatalogFlag);
            // append baseItemDetail to orderDetail
            orderDetail.appendChild (baseItemDetail);

            // increment the counter variable:
            this.counter++;
        } // for iter

        Element buyerExpectedUnitPrice = this.doc.createElement ("BuyerExpectedUnitPrice");
        // <Price>
        Element buyerExpPrice = this.doc.createElement ("Price");
        // <UnitPrice>
        Element buyerExpUnitPrice = this.doc.createElement ("UnitPrice");
        str = lineDataElement.getImportValue ("price");
        if (str != null)
        {
            buyerExpUnitPrice.appendChild (this.doc.createTextNode (str));
        } // if
        this.totalPrice += new  Float (str).doubleValue ();
        buyerExpPrice.appendChild (buyerExpUnitPrice);
        buyerExpectedUnitPrice.appendChild (buyerExpPrice);
        orderDetail.appendChild (buyerExpectedUnitPrice);
        return orderDetail;
    } // getOrderDetail


    /**************************************************************************
     * Creates the OrderSummary area from the XML-Structure from
     * the Business Object Document(BOD)
     * of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     *
     * @return  the generated XML structure
     */
    private  Element createOrderSummary ()
    {
        //    <OrderSummary>
        Element orderSummary = this.doc.createElement ("OrderSummary");
        //<TotalAmount>
        Element totalAmount = this.doc.createElement ("TotalAmount");
        totalAmount.appendChild (this.doc.createTextNode (Float
            .toString (this.totalPrice)));
        orderSummary.appendChild (totalAmount);
        //<TotalLineNum>1</TotalLineNum>
        Element totalLineNum = this.doc.createElement ("TotalLineNum");
        totalLineNum.appendChild (this.doc.createTextNode (Integer
            .toString (this.counter - 1)));
        orderSummary.appendChild (totalLineNum);
        //</OrderSummary>
        return orderSummary;
    } // createOrderSummary


    /**************************************************************************
     * Returns a string representing the  date value in string.
     *
     * @param   d       ???
     *
     * @return a string
     */
    public String dateToString (Date d)
    {
        return DateTimeHelpers.dateTimeToString (d, "yyyyMMdd") + "T" +
            DateTimeHelpers.dateTimeToString (d, "hh:mm:ss");
    } // dateToString

} // XCBLPOFilter

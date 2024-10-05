/*
 * Class: XMLRFCSalesOrderFilter.java
 */

// package:
package ibs.di.filter;

// imports:
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.filter.Filter;

import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/******************************************************************************
 * The XMLRFCSalesOrderFilter is a pure export filter and creates an order
 * in the SAP XML RFC Format. <BR/>
 *
 * @version     $Id: XMLRFCSalesOrderFilter.java,v 1.8 2007/07/31 19:13:55 kreimueller Exp $
 *
 * @author      Bernd Buchegger (BB), 20010112
 ******************************************************************************
 */
public class XMLRFCSalesOrderFilter extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLRFCSalesOrderFilter.java,v 1.8 2007/07/31 19:13:55 kreimueller Exp $";


    /**
     * Node used as insertion point for positions. <BR/>
     */
    public Node insertionPointPositions = null;


    /**************************************************************************
     * Creates an ImportFilter Object. <BR/>
     */
    public XMLRFCSalesOrderFilter ()
    {
        super ();
    } // XMLRFCSalesOrderFilter


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
        Document doc = this.createDocument ();
        // create the envelope:
        Element envelope = doc.createElementNS ("sap-com:document:sap",
            "sap:Envelope");
        envelope.setAttribute ("xmlns:sap", "urn:sap-com:document:sap");
        envelope.setAttribute ("version", "1.0");

        doc.appendChild (envelope);
        // create the body element inside the envelope
        Element body = doc.createElement ("sap:Body");
        envelope.appendChild (body);
        // create the bapi inside the body
        Element bapi = doc.createElementNS (
            "sap-com:document:sap:rfc:functions",
            "rfc:BAPI_SALESORDER_CREATEFROMDATA");
        bapi.setAttribute ("xmlns:rfc",
            "urn:sap-com:document:sap:rfc:functions");
        body.appendChild (bapi);
        // store insertionPoint
        this.p_insertionPoint = bapi;
    } // initExport


    /***************************************************************************
     * Creates the ORDER_HEADER_IN section in the SAP XML RFC structure for
     * salesorders. <BR/> This method uses the data from the DataElement to
     * create the appropriate values in the structure. <BR/>
     *
     * @param dataElement the DataElement that holds the data
     *
     * @return the ORDER_HEADER_IN structure as a DOM element
     */
    private Element createOrderHeaderIn (DataElement dataElement)
    {
        String str;
        Date date;
        //<ORDER_HEADER_IN>
        Element  orderHeaderIn = this.doc.createElement ("ORDER_HEADER_IN");
        //<DOCNUMBER>
        Element  docNumber = this.doc.createElement ("DOC_NUMBER");
        docNumber.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (docNumber);
        //<DOC_TYPE>
        Element docType = this.doc.createElement ("DOC_TYPE");
        docType.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (docType);
        //<COLLECT_NO>
        Element collectNo = this.doc.createElement ("COLLECT_NO");
        collectNo.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (collectNo);
        //<SALES_ORG>
        Element salesOrg = this.doc.createElement ("SALES_ORG");
        salesOrg.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (salesOrg);
        //<DISTR_CHAN>
        Element distrChan = this.doc.createElement ("DISTR_CHAN");
        distrChan.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (distrChan);
        //<DIVISION>
        Element division = this.doc.createElement ("DIVISION");
        division.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (division);
        //<SALES_GRP>
        Element salesGrp = this.doc.createElement ("SALES_GRP");
        salesGrp.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (salesGrp);
        //<SALES_OFF>
        Element salesOff = this.doc.createElement ("SALES_OFF");
        salesOff.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (salesOff);
        //<REQ_DATE_H>
        Element reqDateH = this.doc.createElement ("REQ_DATE_H");
        reqDateH.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (reqDateH);
        //<DATE_TYPE>
        Element dateType = this.doc.createElement ("DATE_TYPE");
        dateType.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (dateType);
        //<PURCH_NO>
        Element purchNo = this.doc.createElement ("PURCH_NO");
        str = dataElement.getImportStringValue ("voucherNo");
        if (str == null)
        {
            str = "";
        } // if
        purchNo.appendChild (this.doc.createTextNode (str));
        orderHeaderIn.appendChild (purchNo);
        //<PURCH_DATE>
        Element purchDate = this.doc.createElement ("PURCH_DATE");
        date = dataElement.getImportDateValue ("voucherDate");
        if (date == null)
        {
            str = "";
        } // if
        else
        {
            str = date.toString ();
        } // else
        purchDate.appendChild (this.doc.createTextNode (str));
        orderHeaderIn.appendChild (purchDate);
        //<PO_METHOD>
        Element poMethod = this.doc.createElement ("PO_METHOD");
        poMethod.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (poMethod);
        //<PO_SUPPLEM>
        Element poSupplem = this.doc.createElement ("PO_SUPPLEM");
        poSupplem.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (poSupplem);
        //<REF_1>
        Element ref1 = this.doc.createElement ("REF_1");
        ref1.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (ref1);
        //<NAME>
        Element name = this.doc.createElement ("NAME");
        name.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (name);
        //<TELEPHONE>
        Element telephone = this.doc.createElement ("TELEPHONE");
        telephone.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (telephone);
        //<PRICE_GRP>
        Element priceGrp = this.doc.createElement ("PRICE_GRP");
        priceGrp.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (priceGrp);
        //<CUST_GROUP>
        Element custGroup = this.doc.createElement ("CUST_GROUP");
        custGroup.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGroup);
        //<SALES_DIST>
        Element salesDist = this.doc.createElement ("SALES_DIST");
        salesDist.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (salesDist);
        //<PRICE_LIST>
        Element priceList = this.doc.createElement ("PRICE_LIST");
        priceList.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (priceList);
        //<INCOTERMS1>
        Element incoTerms1 = this.doc.createElement ("INCOTERMS1");
        incoTerms1.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (incoTerms1);
        //<INCOTERMS2>
        Element incoTerms2 = this.doc.createElement ("INCOTERMS2");
        incoTerms2.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (incoTerms2);
        //<PMNTTRMS>
        Element pmnttrms = this.doc.createElement ("PMNTTRMS");
        pmnttrms.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (pmnttrms);
        //<DLV_BLOCK>
        Element dlvBlock = this.doc.createElement ("DLV_BLOCK");
        dlvBlock.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (dlvBlock);
        //<BILL_BLOCK>
        Element billBlock = this.doc.createElement ("BILL_BLOCK");
        billBlock.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (billBlock);
        //<ORD_REASON>
        Element ordReason = this.doc.createElement ("ORD_REASON");
        ordReason.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (ordReason);
        //<COMPL_DLV>
        Element complDlv = this.doc.createElement ("COMPL_DLV");
        complDlv.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (complDlv);
        //<PRICE_DATE>
        Element priceDate = this.doc.createElement ("PRICE_DATE");
        priceDate.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (priceDate);
        //<QT_VALID_F>
        Element qtValidF = this.doc.createElement ("QT_VALID_F");
        qtValidF.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (qtValidF);
        //<QT_VALID_T>
        Element qtValidT = this.doc.createElement ("QT_VALID_T");
        qtValidT.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (qtValidT);
        //<CT_VALID_F>
        Element ctValidF = this.doc.createElement ("CT_VALID_F");
        ctValidF.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (ctValidF);
        //<CT_VALID_T>
        Element ctValidT = this.doc.createElement ("CT_VALID_T");
        ctValidT.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (ctValidT);
        //<CUST_GRP1>
        Element custGrp1 = this.doc.createElement ("CUST_GRP1");
        custGrp1.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGrp1);
        //<CUST_GRP2>
        Element custGrp2 = this.doc.createElement ("CUST_GRP2");
        custGrp2.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGrp2);
        //<CUST_GRP3>
        Element custGrp3 = this.doc.createElement ("CUST_GRP3");
        custGrp3.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGrp3);
        //<CUST_GRP4>
        Element custGrp4 = this.doc.createElement ("CUST_GRP4");
        custGrp4.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGrp4);
        //<CUST_GRP5>
        Element custGrp5 = this.doc.createElement ("CUST_GRP5");
        custGrp5.appendChild (this.doc.createTextNode (""));
        orderHeaderIn.appendChild (custGrp5);

        return orderHeaderIn;
    } // createOrderHeaderIn


    /**************************************************************************
     * Creates the ORDER_CFSG_INST section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_CFGS_INST structure as a DOM element
     */
    private Element createOrderCfgsInst (DataElement dataElement)
    {
        //<ORDER_CFGS_INST>
        Element  orderCfgsInst = this.doc.createElement ("ORDER_CFGS_INST");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<CONFIG_ID>
        Element configId = this.doc.createElement ("CONFIG_ID");
        configId.appendChild (this.doc.createTextNode (""));
        item.appendChild (configId);
        //<INST_ID>
        Element instId = this.doc.createElement ("INST_ID");
        instId.appendChild (this.doc.createTextNode (""));
        item.appendChild (instId);
        //<OBJ_TYPE>
        Element objType = this.doc.createElement ("OBJ_TYPE");
        objType.appendChild (this.doc.createTextNode (""));
        item.appendChild (objType);
        //<CLASS_TYPE>
        Element classType = this.doc.createElement ("CLASS_TYPE");
        classType.appendChild (this.doc.createTextNode (""));
        item.appendChild (classType);
        //<OBJ_KEY>
        Element objKey = this.doc.createElement ("OBJ_KEY");
        objKey.appendChild (this.doc.createTextNode (""));
        item.appendChild (objKey);
        //<OBJ_TXT>
        Element objTxt = this.doc.createElement ("OBJ_TXT");
        objTxt.appendChild (this.doc.createTextNode (""));
        item.appendChild (objTxt);
        //<QUANTITY>
        Element quantity = this.doc.createElement ("QUANTITY");
        quantity.appendChild (this.doc.createTextNode (""));
        item.appendChild (quantity);
        //<AUTHOR>
        Element author = this.doc.createElement ("AUTHOR");
        author.appendChild (this.doc.createTextNode (""));
        item.appendChild (author);

        orderCfgsInst.appendChild (item);
        return orderCfgsInst;
    } // createOrderCfgsInst


    /**************************************************************************
     * Creates the ORDER_CFSG_INST section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_CFGS_INST structure as a DOM element
     */
    private Element createOrderCfgsPartOf (DataElement dataElement)
    {
        //<ORDER_CFGS_PART_OF>
        Element  orderCfgsPartOf = this.doc.createElement ("ORDER_CFGS_PART_OF");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<CONFIG_ID>
        Element configId = this.doc.createElement ("CONFIG_ID");
        configId.appendChild (this.doc.createTextNode (""));
        item.appendChild (configId);
        //<PARENT_ID>
        Element parentId = this.doc.createElement ("PARENT_ID");
        parentId.appendChild (this.doc.createTextNode (""));
        item.appendChild (parentId);
        //<INST_ID>
        Element instId = this.doc.createElement ("INST_ID");
        instId.appendChild (this.doc.createTextNode (""));
        item.appendChild (instId);
        //<PART_OF_NO>
        Element partOfNo = this.doc.createElement ("PART_OF_NO");
        partOfNo.appendChild (this.doc.createTextNode (""));
        item.appendChild (partOfNo);
        //<OBJ_TYPE>
        Element objType = this.doc.createElement ("OBJ_TYPE");
        objType.appendChild (this.doc.createTextNode (""));
        item.appendChild (objType);
        //<CLASS_TYPE>
        Element classType = this.doc.createElement ("CLASS_TYPE");
        classType.appendChild (this.doc.createTextNode (""));
        item.appendChild (classType);
        //<OBJ_KEY>
        Element objKey = this.doc.createElement ("OBJ_KEY");
        objKey.appendChild (this.doc.createTextNode (""));
        item.appendChild (objKey);
        //<AUTHOR>
        Element author = this.doc.createElement ("AUTHOR");
        author.appendChild (this.doc.createTextNode (""));
        item.appendChild (author);

        orderCfgsPartOf.appendChild (item);
        return orderCfgsPartOf;
    } // createOrderCfgsPartOf


    /**************************************************************************
     * Creates the ORDER_CFGS_REF section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_CFGS_REF structure as a DOM element
     */
    private Element createOrderCfgsRef (DataElement dataElement)
    {
        //<ORDER_CFGS_REF>
        Element  orderCfgsRef = this.doc.createElement ("ORDER_CFGS_REF");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<POSEX>
        Element posex = this.doc.createElement ("POSEX");
        posex.appendChild (this.doc.createTextNode (""));
        item.appendChild (posex);
        //<CONFIG_ID>
        Element configId = this.doc.createElement ("CONFIG_ID");
        configId.appendChild (this.doc.createTextNode (""));
        item.appendChild (configId);
        //<ROOT_ID>
        Element rootId = this.doc.createElement ("ROOT_ID");
        rootId.appendChild (this.doc.createTextNode (""));
        item.appendChild (rootId);

        orderCfgsRef.appendChild (item);
        return orderCfgsRef;
    } // createOrderCfgsRefs


    /**************************************************************************
     * Creates the ORDER_CFGS_VALUE section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_CFGS_VALUE structure as a DOM element
     */
    private Element createOrderCfgsValue (DataElement dataElement)
    {
        //<ORDER_CFGS_VALUE>
        Element  orderCfgsValue = this.doc.createElement ("ORDER_CFGS_VALUE");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<CONFIG_ID>
        Element configId = this.doc.createElement ("CONFIG_ID");
        configId.appendChild (this.doc.createTextNode (""));
        item.appendChild (configId);
        //<INST_ID>
        Element instId = this.doc.createElement ("INST_ID");
        instId.appendChild (this.doc.createTextNode (""));
        item.appendChild (instId);
        //<CHARC>
        Element charc = this.doc.createElement ("CHARC");
        charc.appendChild (this.doc.createTextNode (""));
        item.appendChild (charc);
        //<CHARC_TXT>
        Element charcTxt = this.doc.createElement ("CHARC_TXT");
        charcTxt.appendChild (this.doc.createTextNode (""));
        item.appendChild (charcTxt);
        //<VALUE>
        Element value = this.doc.createElement ("VALUE");
        value.appendChild (this.doc.createTextNode (""));
        item.appendChild (value);
        //<VALUE_TXT>
        Element valueTxt = this.doc.createElement ("VALUE_TXT");
        valueTxt.appendChild (this.doc.createTextNode (""));
        item.appendChild (valueTxt);
        //<AUTHOR>
        Element author = this.doc.createElement ("AUTHOR");
        author.appendChild (this.doc.createTextNode (""));
        item.appendChild (author);

        orderCfgsValue.appendChild (item);
        return orderCfgsValue;
    } // createOrderCfgsValue


    /**************************************************************************
     * Creates the ORDER_ITEMS_IN section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_ITEMS_IN structure as a DOM element
     */
    private Element createOrderItemsIn (DataElement dataElement)
    {
        //<ORDER_ITEMS_IN>
        Element  orderItemsIn = this.doc.createElement ("ORDER_ITEMS_IN");
        // this will be the insertion point to add the order positions
        this.insertionPointPositions = orderItemsIn;
        return orderItemsIn;
    } // createOrderItemsIn


    /**************************************************************************
     * Creates an item in the ORDER_ITEMS_IN section in the SAP XML RFC
     * structure for salesorders and add it to the ORDER_ITEMS_IN node. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     */
    private void createOrderItem (DataElement dataElement)
    {
        String str;

        //<item>
        Element  item = this.doc.createElement ("item");
        //<ITM_NUMBER>
        Element itmNumber = this.doc.createElement ("ITM_NUMBER");
        itmNumber.appendChild (this.doc.createTextNode (""));
        item.appendChild (itmNumber);
        //<HG_LV_ITEM>
        Element hgLvItem = this.doc.createElement ("HG_LV_ITEM");
        hgLvItem.appendChild (this.doc.createTextNode (""));
        item.appendChild (hgLvItem);
        //<PO_ITM_NO>
        Element poItmNo = this.doc.createElement ("PO_ITM_NO");
        poItmNo.appendChild (this.doc.createTextNode (""));
        item.appendChild (poItmNo);
        //<MATERIAL>
        Element material = this.doc.createElement ("MATERIAL");
        str = dataElement.getImportStringValue ("productno");
        if (str == null)
        {
            str = "";
        } // if
        material.appendChild (this.doc.createTextNode (str));
        item.appendChild (material);
        //<CUST_MAT>
        Element custMat = this.doc.createElement ("CUST_MAT");
        custMat.appendChild (this.doc.createTextNode (""));
        item.appendChild (custMat);
        //<BATCH>
        Element batch = this.doc.createElement ("BATCH");
        batch.appendChild (this.doc.createTextNode (""));
        item.appendChild (batch);
        //<DLV_GROUP>
        Element dlvGroup = this.doc.createElement ("DLV_GROUP");
        dlvGroup.appendChild (this.doc.createTextNode (""));
        item.appendChild (dlvGroup);
        //<PART_DLV>
        Element partDlv = this.doc.createElement ("PART_DLV");
        partDlv.appendChild (this.doc.createTextNode (""));
        item.appendChild (partDlv);
        //<REASON_REJ>
        Element reasonRej = this.doc.createElement ("REASON_REJ");
        reasonRej.appendChild (this.doc.createTextNode (""));
        item.appendChild (reasonRej);
        //<BILL_BLOCK>
        Element billBlock = this.doc.createElement ("BILL_BLOCK");
        billBlock.appendChild (this.doc.createTextNode (""));
        item.appendChild (billBlock);
        //<BILL_DATE>
        Element billDate = this.doc.createElement ("BILL_DATE");
        billDate.appendChild (this.doc.createTextNode (""));
        item.appendChild (billDate);
        //<PLANT>
        Element plant = this.doc.createElement ("PLANT");
        plant.appendChild (this.doc.createTextNode (""));
        item.appendChild (plant);
        //<STORE_LOC>
        Element storeLoc = this.doc.createElement ("STORE_LOC");
        storeLoc.appendChild (this.doc.createTextNode (""));
        item.appendChild (storeLoc);
        //<TARGET_QTY>
        Element targetQty = this.doc.createElement ("TARGET_QTY");
        targetQty.appendChild (this.doc.createTextNode (""));
        item.appendChild (targetQty);
        //<TARGET_QU>
        Element targetQu = this.doc.createElement ("TARGET_QU");
        targetQu.appendChild (this.doc.createTextNode (""));
        item.appendChild (targetQu);
        //<REQ_QTY>
        Element reqQty = this.doc.createElement ("REQ_QTY");
        str = dataElement.getImportStringValue ("quantity");
        if (str == null)
        {
            str = "";
        } // if
        reqQty.appendChild (this.doc.createTextNode (str));
        item.appendChild (reqQty);
        //<SALES_UNIT>
        Element salesUnit = this.doc.createElement ("SALES_UNIT");
        str = dataElement.getImportStringValue ("packingUnit");
        if (str == null)
        {
            str = "";
        } // if
        salesUnit.appendChild (this.doc.createTextNode (str));
        item.appendChild (salesUnit);
        //<ITEM_CATEG>
        Element itemCateg = this.doc.createElement ("ITEM_CATEG");
        itemCateg.appendChild (this.doc.createTextNode (""));
        item.appendChild (itemCateg);
        //<SHORT_TEXT>
        Element shortText = this.doc.createElement ("SHORT_TEXT");
        str = dataElement.getImportStringValue ("name");
        if (str == null)
        {
            str = "";
        } // if
        shortText.appendChild (this.doc.createTextNode (str));
        item.appendChild (shortText);
        //<REQ_DATE>
        Element reqDate = this.doc.createElement ("REQ_DATE");
        reqDate.appendChild (this.doc.createTextNode (""));
        item.appendChild (reqDate);
        //<DATE_TYPE>
        Element dateType = this.doc.createElement ("DATE_TYPE");
        dateType.appendChild (this.doc.createTextNode (""));
        item.appendChild (dateType);
        //<REQ_TIME>
        Element reqTime = this.doc.createElement ("REQ_TIME");
        reqTime.appendChild (this.doc.createTextNode (""));
        item.appendChild (reqTime);
        //<COND_TYPE>
        Element condType = this.doc.createElement ("COND_TYPE");
        condType.appendChild (this.doc.createTextNode (""));
        item.appendChild (condType);
        //<COND_VALUE>
        Element condValue = this.doc.createElement ("COND_VALUE");
        condValue.appendChild (this.doc.createTextNode (""));
        item.appendChild (condValue);
        //<COND_P_UNT>
        Element condPUnt = this.doc.createElement ("COND_P_UNT");
        condPUnt.appendChild (this.doc.createTextNode (""));
        item.appendChild (condPUnt);
        //<COND_D_UNT>
        Element condDUnt = this.doc.createElement ("COND_D_UNT");
        condDUnt.appendChild (this.doc.createTextNode (""));
        item.appendChild (condDUnt);
        //<PRC_GROUP1>
        Element prcGroup1 = this.doc.createElement ("PRC_GROUP1");
        prcGroup1.appendChild (this.doc.createTextNode (""));
        item.appendChild (prcGroup1);
        //<PRC_GROUP2>
        Element prcGroup2 = this.doc.createElement ("PRC_GROUP2");
        prcGroup2.appendChild (this.doc.createTextNode (""));
        item.appendChild (prcGroup2);
        //<PRC_GROUP3>
        Element prcGroup3 = this.doc.createElement ("PRC_GROUP3");
        prcGroup3.appendChild (this.doc.createTextNode (""));
        item.appendChild (prcGroup3);
        //<PRC_GROUP4>
        Element prcGroup4 = this.doc.createElement ("PRC_GROUP4");
        prcGroup4.appendChild (this.doc.createTextNode (""));
        item.appendChild (prcGroup4);
        //<PRC_GROUP5>
        Element prcGroup5 = this.doc.createElement ("PRC_GROUP5");
        prcGroup5.appendChild (this.doc.createTextNode (""));
        item.appendChild (prcGroup5);
        //<PROD_HIERA>
        Element prodHiera = this.doc.createElement ("PROD_HIERA");
        prodHiera.appendChild (this.doc.createTextNode (""));
        item.appendChild (prodHiera);
        //<MATL_GROUP>
        Element matlGroup = this.doc.createElement ("MATL_GROUP");
        matlGroup.appendChild (this.doc.createTextNode (""));
        item.appendChild (matlGroup);
        // add the structure to the insertion point
        // which will be the <ORDER_ITEMS_IN> node
        this.insertionPointPositions.appendChild (item);
    } // createOrderItem


    /**************************************************************************
     * Creates the ORDER_ITEMS_OUT section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_ITEMS_OUT structure as a DOM element
     */
    private Element createOrderItemsOut (DataElement dataElement)
    {
        //<ORDER_ITEMS_OUT>
        Element  orderItemsOut = this.doc.createElement ("ORDER_ITEMS_OUT");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<ITM_NUMBER>
        Element itmNumber = this.doc.createElement ("ITM_NUMBER");
        itmNumber.appendChild (this.doc.createTextNode (""));
        item.appendChild (itmNumber);
        //<PO_ITM_NO>
        Element poItmNo = this.doc.createElement ("PO_ITM_NO");
        poItmNo.appendChild (this.doc.createTextNode (""));
        item.appendChild (poItmNo);
        //<MATERIAL>
        Element material = this.doc.createElement ("MATERIAL");
        material.appendChild (this.doc.createTextNode (""));
        item.appendChild (material);
        //<MAT_ENTRD>
        Element matEntrd = this.doc.createElement ("MAT_ENTRD");
        matEntrd.appendChild (this.doc.createTextNode (""));
        item.appendChild (matEntrd);
        //<SHORT_TEXT>
        Element shortText = this.doc.createElement ("SHORT_TEXT");
        shortText.appendChild (this.doc.createTextNode (""));
        item.appendChild (shortText);
        //<NET_VALUE>
        Element netValue = this.doc.createElement ("NET_VALUE");
        netValue.appendChild (this.doc.createTextNode (""));
        item.appendChild (netValue);
        //<CURRENCY>
        Element currency = this.doc.createElement ("CURRENCY");
        currency.appendChild (this.doc.createTextNode (""));
        item.appendChild (currency);
        //<SUBTOTAL_1>
        Element subtotal1 = this.doc.createElement ("SUBTOTAL_1");
        subtotal1.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal1);
        //<SUBTOTAL_2>
        Element subtotal2 = this.doc.createElement ("SUBTOTAL_2");
        subtotal2.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal2);
        //<SUBTOTAL_3>
        Element subtotal3 = this.doc.createElement ("SUBTOTAL_3");
        subtotal3.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal3);
        //<SUBTOTAL_4>
        Element subtotal4 = this.doc.createElement ("SUBTOTAL_4");
        subtotal4.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal4);
        //<SUBTOTAL_5>
        Element subtotal5 = this.doc.createElement ("SUBTOTAL_5");
        subtotal5.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal5);
        //<SUBTOTAL_6>
        Element subtotal6 = this.doc.createElement ("SUBTOTAL_6");
        subtotal6.appendChild (this.doc.createTextNode (""));
        item.appendChild (subtotal6);
        //<SALES_UNIT>
        Element salesUnit = this.doc.createElement ("SALES_UNIT");
        salesUnit.appendChild (this.doc.createTextNode (""));
        item.appendChild (salesUnit);
        //<QTY_REQ_DT>
        Element qtyReqDt = this.doc.createElement ("QTY_REQ_DT");
        qtyReqDt.appendChild (this.doc.createTextNode (""));
        item.appendChild (qtyReqDt);
        //<DLV_DATE>
        Element dlvDate = this.doc.createElement ("DLV_DATE");
        dlvDate.appendChild (this.doc.createTextNode (""));
        item.appendChild (dlvDate);
        //<REPL_TIME>
        Element replTime = this.doc.createElement ("REPL_TIME");
        replTime.appendChild (this.doc.createTextNode (""));
        item.appendChild (replTime);
        //<CONFIGURED>
        Element configured = this.doc.createElement ("CONFIGURED");
        configured.appendChild (this.doc.createTextNode (""));
        item.appendChild (configured);

        orderItemsOut.appendChild (item);
        return orderItemsOut;
    } // createOrderItemsOut


    /**************************************************************************
     * Creates the ORDER_PARTNERS section in the SAP XML RFC structure for
     * salesorders. <BR/>
     * This method uses the data from the DataElement to create the
     * appropriate values in the structure. <BR/>
     *
     * @param dataElement  the DataElement that holds the data
     *
     * @return  the ORDER_PARTNERS structure as a DOM element
     */
    private Element createOrderPartners (DataElement dataElement)
    {
        //<ORDER_PARTNERS>
        Element  orderPartners = this.doc.createElement ("ORDER_PARTNERS");
        // BB: I assume there should be a loop over the items? What is the loop condition
        //<item>
        Element  item = this.doc.createElement ("item");
        //<PARTN_ROLE>
        Element partnRole = this.doc.createElement ("PARTN_ROLE");
        partnRole.appendChild (this.doc.createTextNode (""));
        item.appendChild (partnRole);
        //<PARTN_NUMB>
        Element partnNumb = this.doc.createElement ("PARTN_NUMB");
        partnNumb.appendChild (this.doc.createTextNode (""));
        item.appendChild (partnNumb);
        orderPartners.appendChild (item);
        return orderPartners;
    } // createOrderPartners


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
     * @param   dataElement A DataElement array containing the elements to be
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
        DataElement position;

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
        this.p_insertionPoint.appendChild (this.createOrderHeaderIn (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderCfgsInst (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderCfgsPartOf (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderCfgsRef (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderCfgsValue (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderItemsIn (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderItemsOut (dataElement));
        this.p_insertionPoint.appendChild (this.createOrderPartners (dataElement));

        // check if there are any order positions
        if (dataElement.dataElementList != null)
        {
            // loop through the order positions:
            for (Iterator<DataElement> iter =
                    dataElement.dataElementList.dataElements.iterator ();
                 iter.hasNext ();)
            {
                // get the order position data element
                position = iter.next ();
                // create the order position structure
                this.createOrderItem (position);
            } // for iter
        } // if (dataElement.dataElementList != null)

        // true to indicate that everything is ok*/
        return true;
    } // add

} // XMLRFCSalesOrderFilter

/*
 * Class: OagisPOFilter.java
 */

// package:
package ibs.di.filter;

// imports:
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DataElementList;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.w3c.dom.Element;


/******************************************************************************
 * The m2XMLImportFilter handles all imports from XML datasources that conforms
 * to the m2 import DTD. <BR/>
 *
 * @version     $Id: OagisPOFilter.java,v 1.9 2009/12/22 09:08:26 btatzmann Exp $
 *
 * @author      Buchegger Bernd (BB), 990521
 ******************************************************************************
 */
public class OagisPOFilter extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OagisPOFilter.java,v 1.9 2009/12/22 09:08:26 btatzmann Exp $";



    /**************************************************************************
     * Creates an ImportFilter Object. <BR/>
     */
    public OagisPOFilter ()
    {
        super ();
    } // m2XMLImportFilterXerces


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
     * It`s the beginnig of the XML-dokument.
     * Creates the  DOM root of the XML structure and
     * store the insertionPoint. <BR/>
     */
    private void initExport ()
    {
        // create a new DOM root:
        // <RECEIVE_PO_001>
        this.createDocumentRoot ("RECEIVE_PO_001");
    } // initExport


    /**************************************************************************
     * Creates the ControlArea from the XML-Structure from the Business Objekt
     * Dokument (BOD) of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     * It contains BusinessServiceRequest, Sender and Datetime. <BR/>
     *
     * @param dataElement  with all Values from DataElement
     *
     * @return  the generated XML structure
     */
    private Element createControlArea (DataElement dataElement)
    {
        // <CNTROLAREA>
        Element controlArea = this.doc.createElement ("CNTROLAREA");
        // <BSR>
        Element bsr = this.doc.createElement ("BSR");
        controlArea.appendChild (bsr);
        // <VERB value="RECEIVE">RECEIVE</VERB>
        Element verb = this.doc.createElement ("VERB");
        verb.setAttribute ("value", "RECEIVE");
        verb.appendChild (this.doc.createTextNode ("RECEIVE"));
        bsr.appendChild (verb);
        // <NOUN value="PO">PO</NOUN>
        Element noun = this.doc.createElement ("NOUN");
        noun.setAttribute ("value", "PO");
        noun.appendChild (this.doc.createTextNode ("PO"));
        bsr.appendChild (noun);
        // <REVISION value="001">001</REVISION>
        Element revision = this.doc.createElement ("REVISION");
        revision.setAttribute ("value", "001");
        revision.appendChild (this.doc.createTextNode ("001"));
        bsr.appendChild (revision);

        // <SENDER>
        Element sender = this.doc.createElement ("SENDER");
        controlArea.appendChild (sender);
        // <LOGICALID></LOGICALID>
        Element logicalId = this.doc.createElement ("LOGICALID");
        logicalId.appendChild (this.doc.createTextNode (""));
        sender.appendChild (logicalId);
        // <COMPONENT>m2</COMPONENT>
        Element component = this.doc.createElement ("COMPONENT");
        component.appendChild (this.doc.createTextNode ("m2"));
        sender.appendChild (component);
        // <TASK>PORECEIVE</TASK>
        Element task = this.doc.createElement ("TASK");
        task.appendChild (this.doc.createTextNode ("PORECEIVE"));
        sender.appendChild (task);
        // <REFERENCEID></REFERENCEID>
        Element referenceId = this.doc.createElement ("REFERENCEID");
        referenceId.appendChild (this.doc.createTextNode (""));
        sender.appendChild (referenceId);
        // <CONFIRMATION></CONFIRMATION>
        Element confirmation = this.doc.createElement ("CONFIRMATION");
        confirmation.appendChild (this.doc.createTextNode (""));
        sender.appendChild (confirmation);
        // <LANGUAGE>DE</LANGUAGE>
        Element language = this.doc.createElement ("LANGUAGE");
        language.appendChild (this.doc.createTextNode ("DE"));
        sender.appendChild (language);
        // <CODEPAGE>UTF-8</CODEPAGE>
        Element codepage = this.doc.createElement ("CODEPAGE");
        codepage.appendChild (this.doc.createTextNode (DIConstants.CHARACTER_ENCODING));
        sender.appendChild (codepage);
        // <AUTHID></AUTHID>
        Element authId = this.doc.createElement ("AUTHID");
        authId.appendChild (this.doc.createTextNode (""));
        sender.appendChild (authId);

        // get the creation date of the order out of the dataElement
        Date creationDate = dataElement.getImportDateTimeValue ("voucherDate");
        // construct a dateTime tree
        if (creationDate != null)
        {
            controlArea.appendChild (this.createDateTime ("CREATION", creationDate));
        } // if

        return controlArea;
    } // createControlArea


    /**************************************************************************
     * Creates the BusinessDataArea from the XML-Structure from
     * the Business Objekt Dokument (BOD) of the Open Application Group
     * This method takes the DataElement to create the
     * appropriate XML structure.
     * It contains Receive_Po,Itemline and Itemheader and creates several
     * Itemlines. <BR/>
     *
     * @param dataElement with the Values of Order_01
     *
     * @return  the generated XML structure
     */
    private Element createDataArea (DataElement dataElement)
    {
        String str;

        // <DATAAREA>
        Element dataArea = this.doc.createElement ("DATAAREA");
        // <RECEIVE_PO>
        Element receivePo = this.doc.createElement ("RECEIVE_PO");
        dataArea.appendChild (receivePo);
        // <ITEMHEADER>
        Element itemHeader = this.doc.createElement ("ITEMHEADER");
        receivePo.appendChild (itemHeader);
        // <POID>Order_01.voucherNo</POID>
        Element poId = this.doc.createElement ("POID");
        str = dataElement.getImportValue ("voucherNo");
        if (str != null)
        {
            poId.appendChild (this.doc.createTextNode (str));
        } // if
        itemHeader.appendChild (poId);
        // <PORELEASE></PORELEASE>
        Element poRelease = this.doc.createElement ("PORELEASE");
        poRelease.appendChild (this.doc.createTextNode (""));
        itemHeader.appendChild (poRelease);

        // loop through all orderline and construct
        // an itemLine out of each dateElement
        DataElement posDataElement = null;
        Element itemLine;
        // create a counter for the LINENUM in the ITEMLINE
        int counter = 1;
        // get attributes from the DataElement to use them in the method ceateItemLine
        String delivDesc = dataElement.getImportValue ("shipmentDescription");
        String notPosDesc = dataElement.getImportValue ("notPossibleDescription");
        String suppl = dataElement.getImportValue ("supplier");
        // get the creation date of the order out of the dataElement
        Date needdelvDate = dataElement.getImportDateTimeValue ("deliveryDate");

        // takes all elements out of the dataElementList:
        // a loop to create several itemLines:
        for (Iterator<DataElement> iter = dataElement.dataElementList.dataElements.iterator ();
             iter.hasNext ();)
        {
            // get a dataElement that represents an object:
            posDataElement = iter.next ();
            // create an itemLine out of the DataElement:
            itemLine = this.createItemLine (posDataElement, counter, delivDesc,
                                       notPosDesc, suppl, needdelvDate);
            // add the itemLine to the receivePo
            receivePo.appendChild (itemLine);
            counter++;
        } // for iter

        return dataArea;
    } // createDataArea


    /**************************************************************************
     * Creates an ItemLine which has its values from the OrderElement_01. <BR/>
     *
     * @param   dataElement             with the values of OrderElement_01
     * @param   lineCounter             for the LINENUM to increase
     * @param   shipmentDescription     shipment method
     * @param   notPossibleDescription  text when delivery is not possible
     * @param   supplier                name of the supplier
     * @param   needdelvDate            datetime qualifier.
     *
     * @return  the generated a XML structure from the ItemLine
     */
    private Element createItemLine (DataElement dataElement, int lineCounter,
                                    String shipmentDescription,
                                    String notPossibleDescription,
                                    String supplier, Date needdelvDate)
    {
        String str;

        // <ITEMLINE>
        Element itemLine = this.doc.createElement ("ITEMLINE");
        // <QUANTITY qualifier="ITEM">
        Element quantity = this.doc.createElement ("QUANTITY");
        quantity.setAttribute ("qualifier", "ITEM");
        itemLine.appendChild (quantity);
        // <VALUE>OrderElement_01.quantity</VALUE>
        Element value = this.doc.createElement ("VALUE");
        str = dataElement.getImportValue ("quantity");
        if (str != null)
        {
            value.appendChild (this.doc.createTextNode (str));
        } // if
        quantity.appendChild (value);
        // <NUMOFDEC></NUMOFDEC>
        Element numofDec = this.doc.createElement ("NUMOFDEC");
        numofDec.appendChild (this.doc.createTextNode (""));
        quantity.appendChild (numofDec);
        // <SIGN>+</SIGN>
        Element sign = this.doc.createElement ("SIGN");
        sign.appendChild (this.doc.createTextNode ("+"));
        quantity.appendChild (sign);
        // <UOM>OrderElement_01.packingUnit</UOM>
        Element uom = this.doc.createElement ("UOM");
        str = dataElement.getImportValue ("packingUnit");
        if (str != null)
        {
            uom.appendChild (this.doc.createTextNode (str));
        } // if
        quantity.appendChild (uom);

        // <DATETIME qualifier="NEEDDELV">
        // construct a dateTime tree
        if (needdelvDate != null)
        {
            itemLine.appendChild (this.createDateTime ("NEEDDELV", needdelvDate));
        } // if

        // <LINENUM></LINENUM>
        Element lineNum = this.doc.createElement ("LINENUM");
        lineNum.appendChild (this.doc.createTextNode ("" + lineCounter));
        itemLine.appendChild (lineNum);
        // <SITELEVEL index="1"></SITELEVEL>
        Element siteLevel1 = this.doc.createElement ("SITELEVEL");
        siteLevel1.setAttribute ("index", "1");
        siteLevel1.appendChild (this.doc.createTextNode (""));
        itemLine.appendChild (siteLevel1);
        // <SITELEVEL index="2"></SITELEVEL>
        Element siteLevel2 = this.doc.createElement ("SITELEVEL");
        siteLevel2.setAttribute ("index", "2");
        siteLevel2.appendChild (this.doc.createTextNode (""));
        itemLine.appendChild (siteLevel2);
        // <DISPOSITN>Order_01.deliveryDescription</DISPOSITN>
        Element dispositn = this.doc.createElement ("DISPOSITION");
        if (shipmentDescription != null)
        {
            dispositn.appendChild (this.doc.createTextNode (shipmentDescription));
        } // if
        itemLine.appendChild (dispositn);
        // <ITEMRV></ITEMRV>
        Element itemRv = this.doc.createElement ("ITEMRV");
        itemRv.appendChild (this.doc.createTextNode (""));
        itemLine.appendChild (itemRv);
        // <NOTES>Order_01.notPossibleDescription</NOTES>
        Element notes = this.doc.createElement ("NOTES");
        if (notPossibleDescription != null)
        {
            notes.appendChild (this.doc.createTextNode (notPossibleDescription));
        } // if
        itemLine.appendChild (notes);
        // <OWNRSHPCDE>Order_01.supplier</OWNRSHPCDE>
        Element ownrshpCde = this.doc.createElement ("OWNRSHPCDE");
        if (supplier != null)
        {
            ownrshpCde.appendChild (this.doc.createTextNode (supplier));
        } // if
        itemLine.appendChild (ownrshpCde);
        // <SERIALNUM>OrderElement_01.productno</SERIALNUM>
        Element serialNum = this.doc.createElement ("SERIALNUM");
        str = dataElement.getImportValue ("productno");
        if (str != null)
        {
            serialNum.appendChild (this.doc.createTextNode (str));
        } // if
        itemLine.appendChild (serialNum);
        // <DESCRIPTN>OrderElement_01.name</DESCRIPTN>
        Element descriptn = this.doc.createElement ("DESCRIPTN");
        str = dataElement.getImportValue ("productDescription");
        if (str != null)
        {
            descriptn.appendChild (this.doc.createTextNode (str));
        } // if
        descriptn.appendChild (this.doc.createTextNode (""));
        itemLine.appendChild (descriptn);
        // <ITEM>OrderElement_01.productDescription</ITEM>
        Element item = this.doc.createElement ("ITEM");
        str = dataElement.name;
        if (str != null)
        {
            item.appendChild (this.doc.createTextNode (str));
        } // if
        itemLine.appendChild (item);

        return itemLine;
    } // createItemLine


    /**************************************************************************
     * Creates the subtree of an dateTime definition. <BR/>
     *
     * @param qualifier        qualifier of the datetime section
     * @param date            date object to use the data from
     *
     * @return  the generated dateTime element
     */
    private Element createDateTime (String qualifier, Date date)
    {
        GregorianCalendar calendar = new GregorianCalendar ();
        calendar.setTime (date);
        //e.g. <DATETIME qualifier="CREATION">
        Element dateTime = this.doc.createElement ("DATETIME");
        dateTime.setAttribute ("qualifier", qualifier);
        //e.g. <YEAR>2000</YEAR>
        Element year = this.doc.createElement ("YEAR");
        year.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.YEAR))));
        dateTime.appendChild (year);
        //e.g. <MONTH>06</MONTH>
        Element month = this.doc.createElement ("MONTH");
        month.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.MONTH))));
        dateTime.appendChild (month);
        //e.g. <DAY>19</DAY>
        Element day = this.doc.createElement ("DAY");
        day.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.DAY_OF_MONTH))));
        dateTime.appendChild (day);
        //e.g. <HOUR>17</HOUR>
        Element hour = this.doc.createElement ("HOUR");
        hour.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.HOUR))));
        dateTime.appendChild (hour);
        //e.g. <MINUTE>02</MINUTE>
        Element minute = this.doc.createElement ("MINUTE");
        minute.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.MINUTE))));
        dateTime.appendChild (minute);
        //e.g. <SECOND>59</SECOND>
        Element second = this.doc.createElement ("SECOND");
        second.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.SECOND))));
        dateTime.appendChild (second);
        //e.g. <SUBSECOND>0000</SUBSECOND>
        Element subsecond = this.doc.createElement ("SUBSECOND");
        subsecond.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.MILLISECOND))));
        dateTime.appendChild (subsecond);
        //e.g. <TIMEZONE>-0100</TIMEZONE>
        Element timezone = this.doc.createElement ("TIMEZONE");
        timezone.appendChild (this.doc.createTextNode ("" + (calendar.get (Calendar.ZONE_OFFSET))));
        dateTime.appendChild (timezone);

        return dateTime;
    } // createDateTime



    /**************************************************************************
     * Creates a XML document. <BR/>
     *
     * @param dataElements  a dataElement array to create the export document
     *                      from
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean create (DataElement[] dataElements)
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
     * initialized first. It takes the Control and the DataArea and take it
     * to the indicationPoint(Proceive_Po).
     * The method uses the insertionPoint property in order to determine
     * where to add the object definitions contained in the dataElement array.
     * <BR/>
     *
     * @param dataElements  a dataElement array to add to the export document
     *
     * @return  true if the export document has been created succuessfully
     *          or false otherwise
     */
    public boolean add (DataElement[] dataElements)
    {
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
        // now loop through the dataElements vector
        DataElement dataElement;
        for (int i = 0; i < dataElements.length; i++)
        {
            dataElement = dataElements[i];
            // create the control area
            this.p_insertionPoint.appendChild (this.createControlArea (dataElement));
            // create the data area
            this.p_insertionPoint.appendChild (this.createDataArea (dataElement));
        } // for
        // true to indicate that everything is ok
        return true;
    } // add

} // class OagisPOFilter

/*
 * Class: RightDataElement.java
 */

// package:
package ibs.di;

// imports:
import ibs.BaseObject;
import ibs.di.DataElement;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * The RightDataElement hold the information of an VALUES section from the
 * XML import file. <BR/>
 *
 * @version     $Id: DataElementList.java,v 1.13 2010/12/23 13:08:24 rburgermann Exp $
 *
 * @author      Buchegger Bernd (BB), 990107
 ******************************************************************************
 */
public class DataElementList extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: DataElementList.java,v 1.13 2010/12/23 13:08:24 rburgermann Exp $";


    /**
     *  name attribute of an RIGHT element. <BR/>
     */
    public Vector<DataElement> dataElements;

    /**
     *  Operation for the import of the objects. <BR/>
     */
    public String operation = null;

    
    /**************************************************************************
     * Creates an RightDataElement. <BR/>
     */
    public DataElementList ()
    {
        // call constructor of super class ObjectReference:
        this.dataElements = new Vector<DataElement> ();

    } // DataElementList


    /**************************************************************************
     * Add an DataElement to the list of DataElements. <BR/>
     *
     * @param   dataElement The DataElement to be added to this list.
     */
    public void addElement (DataElement dataElement)
    {
        this.dataElements.addElement (dataElement);
    } // addElement


    /**************************************************************************
     * Find an DataElement by the name of its type.
     * The match in case-insensitive. <BR/>
     *
     * @param typename  the typename to look for in the DataElements vector
     *
     * @return the first DataElement found or null in case we could not find it.
     */
    public DataElement findTypename (String typename)
    {
        DataElement dataElement = null;

        // try to find the related import script
        for (Iterator<DataElement> iter = this.dataElements.iterator (); iter.hasNext ();)
        {
            dataElement = iter.next ();

            // check if this is the DataElement we are looking for:
            if (dataElement.typename.equalsIgnoreCase (typename))
            {
                return dataElement;
            } // if (dataElement.typename.equalsIgnoreCase (typename))
        } // for iter

        return null;
    } // findTypename


    /**************************************************************************
     * Find a DataElement by the code of its type. <BR/>
     * The match is case-insensitive.
     *
     * @param   typeCode    The typecode to look for.
     *
     * @return  The first DataElement found or
     *          <CODE>null</CODE> in case it could not be found.
     */
    public DataElement findTypecode (String typeCode)
    {
        DataElement dataElement = null;

        // try to find the related import script:
        for (Iterator<DataElement> iter = this.dataElements.iterator (); iter.hasNext ();)
        {
            dataElement = iter.next ();

            // check if this is the DataElement we are looking for
            if (dataElement.p_typeCode.equalsIgnoreCase (typeCode))
            {
                return dataElement;
            } // if
        } // for iter

        return null;
    } // findTypecode


    /**************************************************************************
     * Find multiple data elements by the name or code of their type. <BR/>
     * The match is case-insensitive. <BR/>
     *
     * @param   typename    The typename to look for in the DataElements vector.
     *
     * @return  A Vector containing all data elements found or
     *          <CODE>null</CODE> if no element was found.
     */
    public Vector<DataElement> findMultiple (String typename)
    {
        Vector<DataElement> resultDataElements = new Vector<DataElement> ();
        DataElement dataElement;

        // try to find the related data elements:
        for (Iterator<DataElement> iter = this.dataElements.iterator (); iter.hasNext ();)
        {
            dataElement = iter.next ();

            // check if this is one of the data elements we are looking for:
            if (dataElement.typename.equalsIgnoreCase (typename) ||
                dataElement.p_typeCode.equalsIgnoreCase (typename))
            {
                // add the element to the vector:
                resultDataElements.addElement (dataElement);
            } // if
        } // for iter

        // check if there where some elements found:
        if (resultDataElements.size () > 0) // at least one element found?
        {
            // return the vector:
            return resultDataElements;
        } // if at least one element found

        // no elements found
        // return a null value:
        return null;
    } // findMultiple

} // class DataElementList

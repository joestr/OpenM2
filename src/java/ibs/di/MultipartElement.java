/*
 * Class: m2Response.java
 */

// package:
package ibs.di;

// imports:
import ibs.di.ValueDataElement;

import java.util.Vector;


/******************************************************************************
 * The Multipart class holds the data of a multipart response message
 * another application was sending as reply to an data interchange request. <BR/>
 *
 * @version     $Id: MultipartElement.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $
 *
 * @author      Buchegger Bernd (BB), 020507
 ******************************************************************************
 */
public class MultipartElement extends Object
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: MultipartElement.java,v 1.5 2007/08/10 14:56:37 kreimueller Exp $";


    /**
     * general state of the response (Success, Error, Warning, Info). <BR/>
     */
    public int p_responseType = 0;

    /**
     * Code of the error in case there has been one. <BR/>
     */
    public String p_errorCode = "";

    /**
     * Message of the error in case there has been one. <BR/>
     */
    public String p_errorMessage = "";

    /**
     * Reference to the object this part of the response is adressing. <BR/>
     */
    public String p_objectReference = "";

    /**
     * Vector that holds the values in case they have been passed
     * in the response. This will be a vector of ValueDataElement
     * instances. <BR/>
     */
    public Vector<ValueDataElement> p_values = null;


    /**************************************************************************
     * Creates an MultipartElement Object. <BR/>
     */
    public MultipartElement ()
    {
        // nothing to do
    } // MultipartElement


    /**************************************************************************
     * Return the value of a ValueDataElement with a given fieldname. <BR/>
     *
     * @param fieldname         the name of the field
     *
     * @return  the value if found or null otherwise
     */
    public String getValue (String fieldname)
    {
        // try to get the value
        ValueDataElement value = this.getValueDataElement (fieldname);
        // did we found a value?
        if (value != null)
        {
            return value.value;
        } // if

        // did not find any value
        return null;
    } // getValue


    /**************************************************************************
     * Return the valueDataElement with a given fieldname. <BR/>
     *
     * @param fieldname         the name of the field
     *
     * @return  the valueDataElement if found or null otherwise
     */
    public ValueDataElement getValueDataElement (String fieldname)
    {
        ValueDataElement value = null;

        // do we hava a vector with values?
        if (this.p_values != null)
        {
            // loop through the values
            for (int i = 0; i < this.p_values.size (); i++)
            {
                value = this.p_values.elementAt (i);
                if (value.field.equals (fieldname))
                {
                    return value;
                } // if
            } // for (int i = 0; i < multipartElement.p_values.size (); i++)
        } // if (multipartElement != null && multipartElement.p_values != null)
        return value;
    } // getValueDataElement

} // class MultipartElement

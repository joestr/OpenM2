/*
 * Class: ResultElement.java
 */

// package:
package ibs.obj.query;

// imports:
import ibs.BaseObject;
import ibs.obj.query.ResultElement;

import java.util.Vector;


/******************************************************************************
 * ResultRow of a queryexecution on the database. <BR/>
 * Contents all columns of the result as ResultElements. <BR/>
 *
 * @version     $Id: ResultRow.java,v 1.7 2010/04/15 15:31:13 rburgermann Exp $
 *
 * @author      Andreas Jansa (BW) 040201
 ******************************************************************************
 */
public class ResultRow extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ResultRow.java,v 1.7 2010/04/15 15:31:13 rburgermann Exp $";


    /**
     * Resultelements in this row
     */
    Vector<ResultElement> elements = new Vector<ResultElement> ();


    /**************************************************************************
     * Add ResultElement to Row. <BR/>
     *
     * @param   resultElem  the ResultElement to be added to ResultRow. <BR/>
     */
    public void addElement (ResultElement resultElem)
    {
        this.elements.addElement (resultElem);
    } // getName


    /**************************************************************************
     * Get Resultelement on specific position. <BR/>
     *
     * @param   index   position of requiered ResultElement
     *
     * @return  requiered ResultElement
     */
    public ResultElement elementAt (int index)
    {
        return this.elements.elementAt (index);
    } // elementAt


    /**************************************************************************
     * Get count of ResultElements in this ResultRow. <BR/>
     *
     * @return  count of containing ResultElements
     */
    public int getElementCount ()
    {
        return this.elements.size ();
    } // getName


    /**************************************************************************
     * Get Resultelement via it's name. <BR/>
     *
     * @param   name    the name of the required ResultElement. <BR/>
     *
     * @return  null if the required ResultElement was not found,
     *          else the required ResultElement. <BR/>
     */
    public ResultElement getElement (String name)
    {
        ResultElement resultElem = null;

        // search for required ResultElement
        for (int i = 0; i < this.elements.size (); i++)
        {
            resultElem = this.elements.elementAt (i);
            if (name.equals (resultElem.getName ()))
            {
                return resultElem;
            } // if
        } // for

        return null;
    } // getElement


    /**************************************************************************
     * Get value of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  value of required ResultElement
     */
    public String getValue (int index)
    {
        return (this.elements.elementAt (index)).getValue ();
    } // getValue


    /**************************************************************************
     * Get value of specific ResultElement in ResultRow. <BR/>
     *
     * @param   name    name of required ResultElement
     *
     * @return  value of required ResultElement
     */
    public String getValue (String name)
    {
        ResultElement resultElem = null;

        // search for required ResultElement:
        for (int i = 0; i < this.elements.size (); i++)
        {
            resultElem = this.elements.elementAt (i);
            if (name.equals (resultElem.getName ()))
            {
                return resultElem.getValue ();
            } // if
        } // for

        return null;
    } // getValue


    /**************************************************************************
     * Get type of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  type of required ResultElement
     */
    public String getType (int index)
    {
        return (this.elements.elementAt (index)).getType ();
    } // getType

    /**************************************************************************
     * Get multiple attribute of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  multiple attribute
     */
    public boolean getMultipleAttribute (int index)
    {
        return (this.elements.elementAt (index)).getMultiple ();
    } // getMultipleAttribute


    /**************************************************************************
     * Get type of specific ResultElement in ResultRow. <BR/>
     *
     * @param   name    name of required ResultElement
     *
     * @return  type of required ResultElement
     */
    public String getType (String name)
    {
        ResultElement resultElem = null;

        // search for required ResultElement
        for (int i = 0; i < this.elements.size (); i++)
        {
            resultElem = this.elements.elementAt (i);
            if (name.equals (resultElem.getName ()))
            {
                return resultElem.getType ();
            } // if
        } // for

        return null;
    } // getType


    /**************************************************************************
     * Get name of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  type of required ResultElement
     */
    public String getName (int index)
    {
        return (this.elements.elementAt (index)).getName ();
    } // getType


    /**************************************************************************
     * Get DB-Attribute of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  DB-Attribute of required ResultElement
     */
    public String getAttribute (int index)
    {
        return (this.elements.elementAt (index)).getAttribute ();
    } // getType


    /**************************************************************************
     * Get multilang name of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  multilang name of required ResultElement
     */
    public String getMlName (int index)
    {
        return (this.elements.elementAt (index)).getMlName ();
    } // getMlName


    /**************************************************************************
     * Get multilang description of specific ResultElement in ResultRow. <BR/>
     *
     * @param   index   position of required ResultElement
     *
     * @return  multilang description of required ResultElement
     */
    public String getMlDescription (int index)
    {
        return (this.elements.elementAt (index)).getMlDescription ();
    } // getMlDescription

    
    /**************************************************************************
     * get all data within ResultRow in a String. <BR/>
     *
     * @return  String for debugging with all data of this ResultRow. <BR/>
     */
    public String toString ()
    {
        String str = "ResultRow Elements = (";

        // concatinate contents of containing ResultElements to one String
        for (int i = 0; i < this.elements.size (); i++)
        {
            str += (this.elements.elementAt (i)).toString ();
            str += ";";
        } // for

        return str;
    } // toString

} // class ResultRow

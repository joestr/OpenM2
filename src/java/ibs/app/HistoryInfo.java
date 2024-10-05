/*
 * Class: HistoryInfo.java
 */

// package:
package ibs.app;

// imports:
import ibs.IbsObject;
import ibs.bo.OID;
import ibs.io.session.ApplicationInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This is the HistoryInfo Object, which holds the history.
 *
 * @version     $Id: HistoryInfo.java,v 1.19 2009/08/24 07:46:35 btatzmann Exp $
 *
 * @author      Bernhard Walter (BW) 980826
 ******************************************************************************
 */
public class HistoryInfo extends IbsObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: HistoryInfo.java,v 1.19 2009/08/24 07:46:35 btatzmann Exp $";

    /**
     * Denotes if a specific history entry is a query. <BR/>
     */
    public static final String HISTORY_ENTRY_TYPE_QUERY = "query";

    /**************************************************************************
     * This inner class holds the history info for one object. <BR/>
     *
     * @version     $Id: HistoryInfo.java,v 1.19 2009/08/24 07:46:35 btatzmann Exp $
     **************************************************************************
     */
    class _Entry
    {
        /**
         * Version info of the actual class. <BR/>
         * This String contains the version number, date, and author of the last
         * check in to the code versioning system. This is implemented as CVS tag
         * to ensure that it is automatically updated by the cvs system.
         */
        public static final String VERSIONINFO =
            "inner class _Entry: $Id: HistoryInfo.java,v 1.19 2009/08/24 07:46:35 btatzmann Exp $";


        /**
         * Oid of the object.
         */
        OID p_oid;
/*
        / **
         * Name of the tab.
         * /
        String p_tabName;
*/
        /**
         * Id of the tab (either name or id is set).
         */
        int p_tabId;
        /**
         * Icon of the object.
         */
        String p_icon;
        /**
         * Name of the object.
         */
        String p_name;
        /**
         * Id of external objects like exchange or navision objects.
         */
        String p_extId = null;
        /**
         * Type name of the object.
         */
        String p_typeName;

        /**********************************************************************
         * Returns the string representation of this object. <BR/>
         * The type ids and type names are concatenated to create a string
         * representation according to "name (tVersionIdStr): className".
         *
         * @return  String represention of the object.
         */
        public String toString ()
        {
            String theString = "";          // the string

            // compute the string:
            theString += "" + this.p_name + " (" + this.p_oid + ")." +
                        this.p_tabId + "[" + this.p_extId + "]";
            // return the computed string:
            return theString;
        } // toString

        /**********************************************************************
         * Returns the map string representation of this object. <BR/>
         * The name, OID and type are concatenated to create a string
         * in following form "{name: "name",oid:"0x0001",type:"type"}".
         *
         * @return  String Java Script represention of the object.
         */
        public String getMapRepresentation ()
        {
            String name = this.p_name;

            // replace all characters which could cause problems within a java script representation
            name = name.replaceAll ("'", "&#39;");
            name = name.replaceAll ("\"", "&quot;");
            name = name.replaceAll ("\\r\\n", " ");
            name = name.replaceAll ("\\r", " ");
            name = name.replaceAll ("\\n", " ");
            name = name.replaceAll ("\\\\", " ");

            StringBuilder sb = new StringBuilder ("{name: '")
                .append (name)
                .append ("',oid:'")
                .append (this.p_oid)
                .append ("',type:'")
                .append (this.p_typeName)
                .append ("'}");

            return sb.toString ();
        } // getMapRepresentation
    } // class _Entry


    /**
     * The Vector for the history entries. <BR/>
     * Each of these elements must be of class
     * {@link _Entry HistoryInfo$_Entry}.
     */
    private Vector<HistoryInfo._Entry> p_elems = null;

    /**
     * The size of the history, i.e. the actual number of elements. <BR/>
     */
    private int p_size = 0;

    /**
     * Position of the actual element within the vector. <BR/>
     * This position is used for the next () and prev () methods.
     */
    private int p_actPos = 0;

    /**
     * The actual element of the history. <BR/>
     */
    private _Entry p_actElem = null;

    /**
     * Initial size of the history. <BR/>
     */
    private static final int HISTORY_DEFAULT_SIZE = 20;

    /**
     * Holds the size for the history list.
     */
    private int historySize = HistoryInfo.HISTORY_DEFAULT_SIZE;

    /**
     * Increment of the history. <BR/>
     */
    private static final int INCREMENT = 10;


    /**************************************************************************
     * Create a new instance of a history. <BR/>
     * The private property <CODE>p_elems</CODE> is initialized to a new
     * vector. <BR/>
     *
     * @param   app     The global application info object.
     */
    public HistoryInfo (ApplicationInfo app)
    {
        // check if app is set
        if (app != null)
        {
            this.app = app;

            // get configuration variable for historySize:
            Integer historySizeInteger = new Integer (
                this.getConfiguration ().getConfVars ().getValue ("ibsbase.historySize"));

            // check if the history size could be read from the configuration
            if (historySizeInteger != null)
            {
                this.historySize = historySizeInteger.intValue ();
            } // if
        } // check if app != null

        this.p_elems = new Vector<HistoryInfo._Entry> (this.historySize,
                HistoryInfo.INCREMENT);
    } // HistoryInfo


    /**************************************************************************
     * Add an object to the history with a tabname. <BR/>
     *
     * @param   oid         Oid of the object.
     * @param   tabId       Unique id of the tab.
     * @param   name        Name of the object.
     * @param   icon        Icon of the object.
     * @param   externalId  External Id of the external object.
     * @param   typeName    The name of the object's type.
     */
    public void add (OID oid, int tabId, String name, String icon,
                     String externalId, String typeName)
    {
        _Entry entry;                   // the actual entry

        // check if went back to previous entry in the history
        if (this.p_size > 0 &&
            this.p_actElem.p_oid.equals (oid) &&
            (this.p_actElem.p_extId == null || this.p_actElem.p_extId
                .equals (externalId)))
                                        // overwrite actual element?
        {
            // get the actual entry:
            entry = this.p_actElem;
        } // if overwrite actual element
        else                            // do not overwrite actual element
        {
            // new navigation branch so delete the old elements before
            if (this.p_size > (this.p_actPos + 1))
                // currently not at last position?
            {
                // drop all elements after the current one:
                this.p_elems.setSize (this.p_actPos + 1);
                this.p_size = this.p_elems.size (); // store the size
            } // if currently not at last position

            // check if the history buffer is full
            if (this.p_actPos + 1 >= this.historySize)
            {
                // remove the first elem;
                this.p_elems.remove (0);
                this.p_actPos -= 1;
            } // if history buffer is full

            // create a new entry:
            entry = new _Entry ();

            // add the new entry to the vector:
            this.p_elems.addElement (entry);

            // remove old history entries
            if (name != null && name.equals (HistoryInfo.HISTORY_ENTRY_TYPE_QUERY))
            {
                for (int i = 0; i < this.p_elems.size () - 1; i++)
                {
                    if (this.p_elems.get(i).p_name != null &&
                    		this.p_elems.get (i).p_name.equals (HistoryInfo.HISTORY_ENTRY_TYPE_QUERY))
                    {
                        this.p_elems.remove (i);
                        i--;
                    } // if
                } // for i
            } // if

            // get the new vector data:
            this.p_size = this.p_elems.size (); // store the size
            this.last ();               // set to last position
        } // else do not overwrite actual element

        // assign the values to the entry:
        entry.p_oid = oid;
        entry.p_tabId = tabId;
        entry.p_name = name;
        entry.p_icon = icon;
        entry.p_extId = externalId;
        entry.p_typeName = typeName;
    } // add

    /**************************************************************************
     * Add an object to the history with a tabname. <BR/>
     *
     * @param   oid     Oid of the object.
     * @param   tabId   Unieuq id of the tab.
     * @param   name    Name of the object.
     * @param   icon    Icon of the object.
     * @param   typeName The type name of the object.
     */
    public void add (OID oid, int tabId, String name, String icon,
                     String typeName)
    {
        this.add (oid, tabId, name, icon, null, typeName);
    } // add


    /**************************************************************************
     * Set the data for the last object in the history. <BR/>
     * If the oid of the last history entry is the same as the oid parameter
     * of this method then the name and icon of that entry are changed to the
     * new values. <BR/>
     * If the oid is different nothing is done.
     *
     * @param   oid     Oid of the object.
     * @param   name    Name of the object.
     * @param   icon    Icon of the object.
     */
    public void setObjectData (OID oid, String name, String icon)
    {
        // check if the actual element has the same oid:
        if (this.p_actElem != null && this.p_actElem.p_oid.equals (oid) &&
                // and is no query:
                !(this.p_actElem.p_name != null &&
                  this.p_actElem.p_name.equals (HistoryInfo.HISTORY_ENTRY_TYPE_QUERY)))
        {
            // set the entry's data:
            this.p_actElem.p_name = name;
            this.p_actElem.p_icon = icon;
        } // if
    } // setObjectData


    /**************************************************************************
     * Check if there is a previous element before the actual one. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a previous element,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean prevElemExists ()
    {
        if (this.p_actPos > 0)          // there is a previous element?
        {
            return true;                // return state value
        } // if there is a previous element

        return false;                   // return state value
    } // prevElemExists

    /**************************************************************************
     * Check if there is a next element after the actual one. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a next element,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean nextElemExists ()
    {
        if (this.p_actPos < (this.p_elems.size () - 1)) // there is a next element?
        {
            return true;                // return state value
        } // if there is a next element

        return false;                   // return state value
    } // nextElemExists


    /**************************************************************************
     * Get the name of the previous element before the actual one. <BR/>
     *
     * @return  The name if there exists a previous element,
     *          <CODE>null</CODE> otherwise.
     */
    public String getPrevName ()
    {
        if (this.p_actPos > 0)          // there is a previous element?
        {
            // get the name of the previous element and return it:
            return this.p_elems.elementAt (this.p_actPos - 1).p_name;
        } // if there is a previous element

        return null;                    // return state value
    } // getPrevName


    /**************************************************************************
     * Go to the next element in the history. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a next element,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean next ()
    {
        if (this.p_size > (this.p_actPos + 1))
                                        // there is a next element?
        {
            // set the new position:
            this.p_actPos++;
            // get the actual element:
            this.p_actElem = this.p_elems.elementAt (this.p_actPos);

            return true;                // return state value
        } // if there is a next element

        return false;                   // return error value
    } // next


    /**************************************************************************
     * Go to the previous element in the history. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a previous element,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean prev ()
    {
        if (this.p_actPos > 0)          // there is a previous element?
        {
            // set the new position:
            this.p_actPos--;
            // get the actual element:
            this.p_actElem = this.p_elems.elementAt (this.p_actPos);

            return true;                // return state value
        } // if there is a previous element

        return false;                   // return error value
    } // prev


    /**************************************************************************
     * Go to the first element in the history. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a first element (i.e. the history
     *          is not empty), <CODE>false</CODE> otherwise.
     */
    public boolean first ()
    {
        if (this.p_size > 0)            // there is a first element?
        {
            // set the new position:
            this.p_actPos = 0;
            // get the actual element:
            this.p_actElem = this.p_elems.elementAt (this.p_actPos);

            return true;                // return state value
        } // if there is a first element

        return false;                   // return error value
    } // first


    /**************************************************************************
     * Go to the last element in the history. <BR/>
     *
     * @return  <CODE>true</CODE> if there is a last element (i.e. the history
     *          is not empty), <CODE>false</CODE> otherwise.
     */
    public boolean last ()
    {
        if (this.p_size > 0)            // there is a last element?
        {
            // set the new position:
            this.p_actPos = this.p_size - 1;
            // get the actual element:
            this.p_actElem = this.p_elems.elementAt (this.p_actPos);

            return true;                // return state value
        } // if there is a last element

        return false;                   // return error value
    } // last


    /**************************************************************************
     * Go to an element in the history which is a specific offset away from the
     * actual element. <BR/>
     *
     * @param   offset  Offset to be added to the actual position.<BR/>
     *                  <PRE>
     *                  <CODE>offset > 0</CODE> ... go forward<BR/>
     *                  <CODE>offset < 0</CODE> ... go backward<BR/>
     *                  <CODE>offset = 0</CODE> ... position stays unchanged<BR/>
     *                  </PRE>
     *
     * @return  <CODE>true</CODE> if the requested element exists,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean go (int offset)
    {
        if (offset == 0)                // position stays unchanged?
        {
            return true;                // return state value
        } // if position stays unchanged
        else if ((this.p_actPos + offset) >= -1 &&
            this.p_size > (this.p_actPos + offset))
                                        // element exists?
        {
            // Check if a navigation from the first element further has been performed
            if (this.p_actPos + offset < 0)
            {
                return false;
            } // if

            // set the new position:
            this.p_actPos += offset;

            // get the actual element:
            this.p_actElem = this.p_elems.elementAt (this.p_actPos);

            return true;                // return state value
        } // if element exists

        return false;                   // return error value
    } // go


    /**************************************************************************
     * Get the size of the history. <BR/>
     *
     * @return  The size of the history (i.e. the number of entries).
     */
    public int size ()
    {
        // get the actual size of the history and return this value:
        return this.p_size;
    } // size


    /**************************************************************************
     * Get the oid of the actual element in the history. <BR/>
     *
     * @return  Oid of the actual element or
     *          <CODE>null</CODE> if there is no actual element.
     */
    public OID getOid ()
    {
        if (this.p_actElem != null)     // actual element exists?
        {
            // return the oid of the actual element:
            return this.p_actElem.p_oid;
        } // if actual element exists

        // no actual element
        return null;                    // return error value
    } // getOid


    /**************************************************************************
     * Get the tabname of the actual object. <BR/>
     *
     * @return  Tab name of the object which was last visited.
     */
    public String getName ()
    {
        if (this.p_actElem != null)     // actual element exists?
        {
            // return the name of the actual element:
            return this.p_actElem.p_name;
        } // if actual element exists

        // no actual element
        return null;                    // return error value
    } // getName


    /**************************************************************************
     * Get the tab id of the actual object. <BR/>
     *
     * @return  Tab id of the object which was last visited.
     */
    public int getTabId ()
    {
        if (this.p_actElem != null)     // actual element exists?
        {
            // return the tab id of the actual element:
            return this.p_actElem.p_tabId;
        } // if actual element exists

        // no actual element
        return -1;                      // return error value
    } // getTabId


    /**************************************************************************
     * Get the external id of the actual object. <BR/>
     *
     * @return  External Id of the external object which was last visited.
     */
    public String getExtId ()
    {
        if (this.p_actElem != null)     // actual element exists?
        {
            // return the tab id of the actual element:
            return this.p_actElem.p_extId;
        } // if actual element exists

        // no actual element
        return null;                    // return error value
    } // getTabId

    /**************************************************************************
     * Get the actual index. <BR/>
     *
     * @return  Actual index of the history.
     */
    public int getActIndex ()
    {
        // return the actual index of the history
        return this.p_actPos;
    } // getActIndex


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The tab ids and tab names are concatenated to create a string
     * representation according to "{{id1, name1}, {id2, name2}, ...}".
     *
     * @return  String represention of the object.
     */
    public String toString ()
    {
        String theString = "{";         // the string
        String sep = "";                // the element separator
        int pos = 0;                    // position of the tab

        // start the list:
        theString = "history {";

        // loop through the tabs and append their string representation to this
        // object's string:
        for (pos = 0; pos < this.p_size; pos++)
        {
            // append the current element to the string:
            theString += sep + this.p_elems.elementAt (pos);
            sep = ", ";
        } // for

        // finish the list:
        theString += "}";

        // return the computed string:
        return theString;
    } // toString

    /**************************************************************************
     * Returns a collection of all elements with their map representaiton.<BR/>
     * The map representation of a history elem looks like:
     * {name: "name",oid:"0x0001",type:"type"}
     *
     * @return  Collection with map representions of all history elems.
     */
    public Collection<String> getAllElemsMapReprColl ()
    {
        Collection<String> retCol = new ArrayList<String> ();

        // iterate through all elems and add their map representation String to the collection
        Iterator<HistoryInfo._Entry> it = this.p_elems.iterator ();
        while (it.hasNext ())
        {
            retCol.add (it.next ().getMapRepresentation ());
        } // while

        // return the collection
        return retCol;
    } // getAllElemsMapReprColl

} // class HistoryInfo

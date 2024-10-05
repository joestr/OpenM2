/*
 * Class: TabContainer.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 11, 2001
 * Time: 10:28:42 PM
 */

// package:
package ibs.bo.tab;

// imports:
import ibs.bo.OID;
import ibs.bo.tab.Tab;
import ibs.service.user.User;
import ibs.util.list.ElementContainer;
import ibs.util.list.ListException;

import java.util.Enumeration;


/******************************************************************************
 * This class contains all data regarding a set of tabs. <BR/>
 *
 * @version     $Id: TabContainer.java,v 1.12 2007/07/27 12:01:42 kreimueller Exp $
 *
 * @author      kreimueller, 011211
 ******************************************************************************
 */
public class TabContainer extends ElementContainer<Tab>
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: TabContainer.java,v 1.12 2007/07/27 12:01:42 kreimueller Exp $";


    /**
     * The oid of the object for which this container represents the tab bar.
     * <BR/>
     */
    private OID p_oid = null;

    /**
     * The user for whom this is the specific tab bar of the object. <BR/>
     */
    private User p_user = null;

    /**
     * The active tab. <BR/>
     */
    private Tab p_activeTab = null;

    /**
     * Display the state within the buttons. <BR/>
     * Default: <CODE>true</CODE>
     */
    private boolean p_showButtonsLoading = true;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a TabContainer object. <BR/>
     * This constructor calls the constructor of the super class. <P>
     * The private property p_oid is initialized to <CODE>null</CODE>. <BR/>
     * The private property p_user is initialized to <CODE>null</CODE>. <BR/>
     * The private property p_activeTab is initialized to <CODE>null</CODE>. <BR/>
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public TabContainer ()
        throws ListException
    {
        // call constructor of super class:
        super ();

        // initialize the instance's properties:
        this.p_oid = null;
        this.p_user = null;
        this.p_activeTab = null;
    } // TabContainer


    /**************************************************************************
     * Creates a TabContainer object. <BR/>
     * This constructor calls the constructor of the super class. <P>
     * The private property p_activeTab is initialized to <CODE>null</CODE>. <BR/>
     *
     * @param   oid     The oid of the business object to which the tab
     *                  container belongs.
     * @param   user    The user to whom the tab container belongs
     *
     * @throws  ListException
     *          An error occurred during initializing the container.
     */
    public TabContainer (OID oid, User user)
        throws ListException
    {
        // call constructor of super class:
        super ();

        // set the instance's properties:
        this.p_oid = oid;
        this.p_user = user;

        // initialize the other instance properties:
        this.p_activeTab = null;
    } // TabContainer


    ///////////////////////////////////////////////////////////////////////////
    // functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Initialize the element class. <BR/>
     * This method shall be overwritten in sub classes.
     *
     * @throws  ListException
     *          The class could not be initialized.
     *
     * @see ibs.util.list.ElementContainer#setElementClass (Class)
     */
    protected void initElementClass ()
        throws ListException
    {
        this.setElementClass (Tab.class);
    } // initElementClass


    /**************************************************************************
     * Add a new element to the container. <BR/>
     *
     * @param   elem    The element to be added.
     *
     * @return  <CODE>true</CODE> if the element was successfully added,
     *          <CODE>false</CODE> otherwise.
     */
    public final boolean add (Tab elem)
    {
        // call method of super class:
        boolean retVal = super.add (elem);

        // set active tab:
        if (this.p_activeTab == null)   // this is the only tab?
        {
            // make this tab active:
            this.p_activeTab = elem;
        } // if this is the only tab

        // return the result:
        return retVal;
    } // add


    /**************************************************************************
     * Derive a tab from the tab code. <BR/>
     *
     * @param   domainId    The id of the domain where the tab is allowed.
     * @param   code        The (unique) tab code.
     *
     * @return  The found tab or <CODE>null</CODE> if it was not found.
     */
    public final Tab find (int domainId, String code)
    {
        Tab tab = null;                 // the found tab
        boolean found = false;          // was there a type found?

        // loop through the tabs and search for the tab with the correct code:
        // loop through all elements of the list:
        for (Enumeration<Tab> elems = this.elements ();
             !found && elems.hasMoreElements ();)
        {
            // get the element out of the list:
            tab = elems.nextElement ();

            // check if the code is the one we are searching for:
            // compare the tab code and possibly the domain id. The domain id
            // is relevant if it is not 0.
            found = code.equals (tab.getCode ()) &&
                     ((tab.getDomainId () == 0 || domainId == 0) ?
                      true : tab.getDomainId () == domainId);
        } // for

        if (found)                      // the tab was found?
        {
            return tab;                 // return the tab
        } // if the tab was found

        // the tab was not found
        return null;                    // return the error code
    } // find


    /**************************************************************************
     * Derive a tab from the tab oid. <BR/>
     *
     * @param   domainId    The id of the domain where the tab is allowed.
     * @param   oid         The oid of the object representing the tab.
     *
     * @return  The found tab or <CODE>null</CODE> if it was not found.
     */
    public final Tab findOid (int domainId, OID oid)
    {
        Tab tab = null;                 // the found tab
        boolean found = false;          // was there a tab found?

        // check if the oid is valid:
        if (oid != null)                // valid oid?
        {
            // loop through the tabs and search for the tab with the correct code:
            // loop through all elements of the list:
            for (Enumeration<Tab> elems = this.elements ();
                 !found && elems.hasMoreElements ();)
            {
                // get the element out of the list:
                tab = elems.nextElement ();

                // check if the code is the one we are searching for:
                // compare the tab oid and possibly the domain id. The domain id
                // is relevant if it is not 0.
                found = oid.equals (tab.getOid ()) &&
                         ((tab.getDomainId () == 0 || domainId == 0) ?
                          true : tab.getDomainId () == domainId);
            } // for
        } // if valid oid

        if (found)                      // the tab was found?
        {
            return tab;                 // return the tab
        } // if the tab was found

        // the tab was not found
        return null;                    // return the error code
    } // findOid


    /**************************************************************************
     * Derive a type name from the type code. <BR/>
     *
     * @param   code    The (unique) type code.
     *
     * @return  The type name or <CODE>null</CODE> if no type was found.
     */
    public final String getTabName (String code)
    {
        Tab tab = null;                 // the found tab

        // search for the tab:
        tab = this.find (code);

        if (tab != null)                // the tab was found?
        {
            return tab.getName ();      // return the tab name
        } // if the tab was found

        // the tab was not found
        return null;                    // return error code
    } // getTabName


    /**************************************************************************
     * Ensure that there are no elements within the element container. <BR/>
     */
    public void clear ()
    {
        // call method of super class:
        super.clear ();

        // re-initialize the other properties:
        this.p_activeTab = null;
    } // clear


    /**************************************************************************
     * Builds the call for creating the tab bar within a JavaScript code block.
     * <BR/>
     * This call must be included into a java script like
     * <CODE>script.addScript (tabBar.buildJavaScriptCall ());</CODE>. <BR/>
     *
     * @return  The call for creating the tab bar.
     */
    public final String buildJavaScriptCall ()
    {
        StringBuffer call = new StringBuffer (); // the resulting call
        Tab tab = null;                 // the actual tab
        int activeId = 0;               // the id of the active tab
        Enumeration<Tab> tabEnum = null; // all available tabs

        // check if there is an explicit active tab:
        if (this.p_activeTab == null && this.size () > 0)
                                        // no active tab, there are some tabs?
        {
            // get the tabs:
            tabEnum = this.elements ();

            // check if there is at least one tab:
            if (tabEnum.hasMoreElements ()) // at least one tab exists?
            {
                //  make the first tab active:
                this.p_activeTab = tabEnum.nextElement ();
            } // if at least one tab exists
        } // else if no active tab, there are some tabs

        // check if there is an explicit active tab:
        if (this.p_activeTab != null)   // there is an active tab?
        {
            activeId = this.p_activeTab.getIdInt ();
        } // if there is an active tab

        // start the call:
        call.append ("top.createTabBar (")
            .append ("\'").append (String.valueOf (this.p_oid)).append ("\',")
            .append (activeId).append (",").append (this.p_showButtonsLoading);

        // loop through the tabs and append their ids to the call:
        // loop through all elements of the list:
        for (tabEnum = this.elements (); tabEnum.hasMoreElements ();)
        {
            // get the actual tab:
            tab = tabEnum.nextElement ();

            if (tab != null)        // got the tab?
            {
                // append the current element to the string:
                call.append (",").append (tab.getId ());
            } // if got the tab
        } // for

        // finish the call:
        call.append (");");

        // return the computed call:
        return call.toString ();
    } // buildJavaScriptCall


    /**************************************************************************
     * Get the oid of the tab object. <BR/>
     *
     * @return  The oid or <CODE>null</CODE> if there is no oid defined.
     */
    public OID getOid ()
    {
        // get the value and return it:
        return this.p_oid;
    } // getOid


    /**************************************************************************
     * Get the user to whom this tab belongs. <BR/>
     *
     * @return  The user or <CODE>null</CODE> if the tab belongs to no user.
     */
    public final User getUser ()
    {
        // get the value and return it:
        return this.p_user;
    } // getUser


    /**************************************************************************
     * Get the active tab. <BR/>
     *
     * @return  The currently active tab or <CODE>null</CODE> if no tab is
     *          active.
     */
    public final Tab getActiveTab ()
    {
        // get the value and return it:
        return this.p_activeTab;
    } // getActiveTab


    /**************************************************************************
     * Get if the buttons loading state shall be displayed. <BR/>
     *
     * @return  <CODE>true</CODE> if the buttons shall be displayed loading,
     *          <CODE>false</CODE> else.
     */
    public boolean showButtonsLoading ()
    {
        // get the value and return it:
        return this.p_showButtonsLoading;
    } // showButtonsLoading


    /**************************************************************************
     * Set if the buttons loading state shall be displayed. <BR/>
     *
     * @param   showButtonsLoading  <CODE>true</CODE> if the buttons shall be
     *                              diaplayed loading, <CODE>false</CODE>
     *                              otherwise.
     */
    public void setShowButtonsLoading (boolean showButtonsLoading)
    {
        // set the value:
        this.p_showButtonsLoading = showButtonsLoading;
    } // setShowButtonsLoading


    /**************************************************************************
     * Set a new active tab. <BR/>
     * Note: There is no check made if the tab exists wíthin the tab container!
     *
     * @param   tab     The tab to be set to active.
     */
    public final void setActiveTab (Tab tab)
    {
        // set the active tab:
        this.p_activeTab = tab;
    } // setActiveTab


    /**************************************************************************
     * Set a new active tab defined through its domainId and code. <BR/>
     * If the tab is not found within the container nothing is done.
     *
     * @param   domainId    The id of the domain where the tab is allowed.
     * @param   code        The (unique) tab code.
     */
    public final void setActiveTab (int domainId, String code)
    {
        // find the tab:
        Tab tab = this.find (domainId, code);

        // check if the tab was found:
        if (tab != null)                // the tab was found?
        {
            // set the active tab:
            this.p_activeTab = tab;
        } // if the tab was found
    } // setActiveTab


    /**************************************************************************
     * Set a new active tab defined through its domainId and the oid of the
     * object which represents the tab. <BR/>
     * If the tab is not found within the container nothing is done.
     *
     * @param   domainId    The id of the domain where the tab is allowed.
     * @param   oid         The oid of the object representing the tab.
     */
    public final void setActiveTab (int domainId, OID oid)
    {
        // find the tab:
        Tab tab = this.findOid (domainId, oid);

        // check if the tab was found:
        if (tab != null)                // the tab was found?
        {
            // set the active tab:
            this.p_activeTab = tab;
        } // if the tab was found
    } // setActiveTab


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The system values are concatenated to create a string
     * representation according to "Tabs superString". superString is the
     * result of <CODE>super.toString ()</CODE>.
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return "Tabs " + super.toString ();
    } // toString

} // class TabContainer

/*
 * Class: Tab.java
 */
/*
 * Created by IntelliJ IDEA.
 * User: kreimueller
 * Date: Dec 11, 2001
 * Time: 10:28:36 PM
 */

// package:
package ibs.bo.tab;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.bo.OID;
import ibs.bo.tab.TabConstants;
import ibs.util.list.Element;
import ibs.util.list.IElementId;


/******************************************************************************
 * This class represents a tab within the system. <BR/>
 *
 * @version     $Id: Tab.java,v 1.7 2007/07/10 22:40:02 kreimueller Exp $
 *
 * @author      kreimueller, 011211
 ******************************************************************************
 */
public class Tab extends Element
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Tab.java,v 1.7 2007/07/10 22:40:02 kreimueller Exp $";


    /**
     * Version of the type. <BR/>
     * This value may be 0 if the tab is valid within all domains.
     */
    private int p_domainId = 0;

    /**
     * The code of the tab. <BR/>
     * This code is unique within a domain.
     */
    private String p_code = null;

    /**
     * The kind of the tab. <BR/>
     */
    private int p_kind = 0;

    /**
     * The function to be performed when the tab is pressed. <BR/>
     */
    private int p_fct = AppFunctions.FCT_NOFUNCTION;

    /**
     * The permissions which are necessary to display the tab. <BR/>
     * May be 0 to indicate that there are no specific permissions necessary.
     */
    private int p_rights = 0;

    /**
     * Oid of the object which implements the tab. <BR/>
     * This property is used if the tab represents the tabs for a specific
     * object and the tab is a link tab or an object tab.
     */
    private OID p_oid = null;

    /**
     * Number of elements within the tab. <BR/>
     * This property is used if the tab represents the tabs for a specific
     * object and the tab is a container type tab.
     */
    private int p_countElems = 0;

    /**
     * Class to show tab. <BR/>
     * This property contains the fully qualified class name of the class which
     * is used to display the tab, e.g. <CODE>ibs.bo.BusinessObject</CODE>.
     */
    private String p_className = null;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates a Tab object. <BR/>
     *
     * @param   id          Id of the element.
     * @param   name        The element's name.
     */
    public Tab (IElementId id, String name)
    {
        // call constructor of super class:
        super (id, name);

        // set the instance's properties:
    } // Tab


    /**************************************************************************
     * Creates a Tab object. <BR/>
     * Calls the constructor of the super class. <P>
     * The private property p_domainId is set to <CODE>0</CODE>. <BR/>
     * The private property p_code is set to <CODE>null</CODE>. <BR/>
     * The private property p_kind is set to
     *      {@link ibs.bo.tab.TabConstants#TK_DEFAULT TabConstants.TK_DEFAULT}. <BR/>
     * The private property p_fct is set to
     *      {@link ibs.app.AppFunctions#FCT_SHOWOBJECT AppFunctions.FCT_SHOWOBJECT}. <BR/>
     * The private property p_rights is set to <CODE>0</CODE>. <BR/>
     * The private property p_oid is set to <CODE>null</CODE>. <BR/>
     * The private property p_countElems is set to <CODE>0</CODE>. <BR/>
     * The private property p_className is set to <CODE>null</CODE>. <BR/>
     *
     * @param   id      Id of the tab.
     */
    public Tab (int id)
    {
        // call constructor of super class:
        super (id, null);

        // initialize the object's properties:
        this.p_domainId = 0;
        this.p_code = null;
        this.p_kind = TabConstants.TK_DEFAULT;
        this.p_fct = AppFunctions.FCT_SHOWOBJECT;
        this.p_rights = 0;
        this.p_oid = null;
        this.p_countElems = 0;
        this.p_className = null;
    } // Tab


    /**************************************************************************
     * Creates a Type object. <BR/>
     * Calls the constructor of the super class. <P>
     * The private property p_oid is set to <CODE>null</CODE>. <BR/>
     * The private property p_countElems is set to <CODE>0</CODE>. <BR/>
     *
     * @param   id          Id of the tab.
     * @param   domainId    Domain where the tab is valid.
     * @param   code        The tab's unique code.
     * @param   name        The tab's name.
     * @param   kind        The kind of the tab.
     * @param   fct         The function to be performed when tab is selected.
     * @param   rights      The permissions which are necessary to display the
     *                      tab.
     * @param   className   The class to show the view tab. This is a fully
     *                      qualified class name, e.g.
     *                      <CODE>ibs.bo.BusinessObject</CODE>.
     */
    public Tab (int id, int domainId, String code, String name, int kind,
                int fct, int rights, String className)
    {
        // call constructor of super class:
        super (id, name);

        // set the instance's properties:
        this.p_domainId = domainId;
        this.p_code = code;
        this.p_kind = kind;
        this.p_fct = fct;
        this.p_rights = rights;
        this.p_className = className;

        // initialize the other instance properties:
        this.p_oid = null;
        this.p_countElems = 0;
    } // Tab


    /**************************************************************************
     * Creates a Type object. <BR/>
     *
     * @param   id          Id of the tab.
     * @param   domainId    Domain where the tab is valid.
     * @param   code        The tab's unique code.
     * @param   kind        The kind of the tab.
     * @param   fct         The function to be performed when tab is selected.
     * @param   oid         The oid of the tab object.
     * @param   rights      The permissions which are necessary to display the
     *                      tab.
     * @param   countElems  The number of elements within the tab.
     * @param   className   The class to show the view tab. This is a fully
     *                      qualified class name, e.g.
     *                      <CODE>ibs.bo.BusinessObject</CODE>.
     */
    public Tab (int id, int domainId, String code, int kind,
                int fct, OID oid, int rights, int countElems, String className)
    {
        // call constructor of super class:
        super (id, code);

        // set the instance's properties:
        this.p_domainId = domainId;
        this.p_code = code;
        this.p_kind = kind;
        this.p_fct = fct;
        this.p_oid = oid;
        this.p_rights = rights;
        this.p_countElems = countElems;
        this.p_className = className;

        // initialize the other instance properties:
    } // Tab



    ///////////////////////////////////////////////////////////////////////////
    // other methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the domain id of the tab. <BR/>
     *
     * @return  The domain id.
     */
    public final int getDomainId ()
    {
        // get the value and return it:
        return this.p_domainId;
    } // getDomainId


    /**************************************************************************
     * Get the code of the tab. <BR/>
     *
     * @return  The code.
     */
    public final String getCode ()
    {
        // get the value and return it:
        return this.p_code;
    } // getCode


    /**************************************************************************
     * Get the oid of the tab. <BR/>
     *
     * @return  The oid.
     */
    public final OID getOid ()
    {
        // get the value and return it:
        return this.p_oid;
    } // getOid


    /**************************************************************************
     * Get the kind of the tab. <BR/>
     *
     * @return  The tab kind.
     */
    public final int getKind ()
    {
        // get the value and return it:
        return this.p_kind;
    } // getKind


    /**************************************************************************
     * Get the function which corresponds to the tab. <BR/>
     *
     * @return  The function value.
     */
    public final int getFct ()
    {
        // get the value and return it:
        return this.p_fct;
    } // getFct


    /**************************************************************************
     * Get the rights of the tab. <BR/>
     *
     * @return  The rights.
     */
    public final int getRights ()
    {
        // get the value and return it:
        return this.p_rights;
    } // getRights


    /**************************************************************************
     * Get the number of elements within the tab. <BR/>
     *
     * @return  The number of elements.
     */
    public final int getCountElems ()
    {
        // get the value and return it:
        return this.p_countElems;
    } // getCountElems


    /**************************************************************************
     * Get the class which is used to display the tab. <BR/>
     *
     * @return  The class name or <CODE>null</CODE> if there is no class name
     *          defined.
     */
    public final String getClassName ()
    {
        // get the value and return it:
        return this.p_className;
    } // getClassName


    /**************************************************************************
     * Returns the string representation of this object. <BR/>
     * The domain id, the code and the id are concatenated to create a string
     * representation according to "domainId.code (id)".
     *
     * @return  String represention of the object.
     */
    public final String toString ()
    {
        // compute the string and return it:
        return this.p_domainId  + "." + this.p_code + " (" + this.getId () + ")";
    } // toString

} // class Tab

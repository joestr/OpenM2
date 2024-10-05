/*
 * Class: CatalogContainer_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;


/******************************************************************************
 * This class represents one container object of type Store with version 01.
 * <BR/>
 *
 * @version     $Id: CatalogContainer_01.java,v 1.14 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Bernhard Walter (BW), 980929
 ******************************************************************************
 */
public class CatalogContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: CatalogContainer_01.java,v 1.14 2010/04/13 15:55:57 rburgermann Exp $";
    
    /**
     * Headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_CATALOGCONTAINER =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_DESCRIPTION,
        BOTokens.ML_TYPE,
        BOTokens.ML_OWNER,
        BOTokens.ML_CHANGED,
    }; // LST_HEADINGS_CATALOGCONTAINER

    /**
     * Reduced headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_CATALOGCONTAINER_REDUCED =
    {
        CatalogContainer_01.LST_HEADINGS_CATALOGCONTAINER[0],
        CatalogContainer_01.LST_HEADINGS_CATALOGCONTAINER[1],
    }; // LST_HEADINGS_CATALOGCONTAINER_REDUCED

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_CATALOGCONTAINER =
    {
        CatalogContainer_01.FIELD_NAME,
        CatalogContainer_01.FIELD_DESCRIPTION,
        "typeName",
        "owner",
        "lastChanged",
    }; // LST_ORDERINGS_CATALOGCONTAINER

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_CATALOGCONTAINER_REDUCED =
    {
        CatalogContainer_01.LST_ORDERINGS_CATALOGCONTAINER[0],
        CatalogContainer_01.LST_ORDERINGS_CATALOGCONTAINER[1],
    }; // LST_ORDERINGS_CATALOGCONTAINER_REDUCED

    
    /**
     * Field name: name. <BR/>
     */
    private static final String FIELD_NAME = "name";
    /**
     * Field name: description. <BR/>
     */
    private static final String FIELD_DESCRIPTION = "description";


    /**************************************************************************
     * This constructor creates a new instance of the class CatalogContainer_01.
     * <BR/>
     */
    public CatalogContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // CatalogContainer_01


    /**************************************************************************
     * This constructor creates a new instance of the class Store.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public CatalogContainer_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // CatalogContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set the instance's attributes:
        this.viewContent = "v_Container$content";
        this.elementClassName = "m2.store.CatalogContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Set the headings for this container. This method MUST be overloaded if. <BR/>
     * you have your own subclass of ContainerElement and if you need other headings. <BR/>
     * You have to overload the method setOrderings () as well.
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
            // don't show the extended attributes
        {
            // set headings
            this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
                CatalogContainer_01.LST_HEADINGS_CATALOGCONTAINER_REDUCED, env);

            // set orderings
            this.orderings = CatalogContainer_01.LST_ORDERINGS_CATALOGCONTAINER_REDUCED;
        } // if
        else
        {
            // set headings
            this.headings = MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE, 
                CatalogContainer_01.LST_HEADINGS_CATALOGCONTAINER, env);

            // set orderings
            this.orderings = CatalogContainer_01.LST_ORDERINGS_CATALOGCONTAINER;
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object info view. <BR/>
     *
     * @return  An array with button ids that can be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
            Buttons.BTN_DELETE,
//            Buttons.BTN_CUT,
//            Buttons.BTN_COPY,
//            Buttons.BTN_DISTRIBUTE,
//            Buttons.BTN_CLEAN,
//            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
            Buttons.BTN_NEW,
//            Buttons.BTN_PASTE,
//            Buttons.BTN_CLEAN,
            Buttons.BTN_SEARCH,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_LIST_COPY,
            Buttons.BTN_LIST_CUT,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons

} // class CatalogContainer_01

/*
 * Class: PersonContainer_01.java
 */

// package:
package m2.mad;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;


/******************************************************************************
 * This class represents one object of type PersonContainer with version 01. <BR/>
 *
 * @version     $Id: PersonContainer_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 980603
 ******************************************************************************
 */
public class PersonContainer_01 extends MasterDataContainer_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PersonContainer_01.java,v 1.14 2010/04/13 15:55:58 rburgermann Exp $";

    /**
     * Reduced headings of columns. <BR/>
     * These headings are shown at the top of lists.
     */
    public static final String [] LST_HEADINGS_PERSONCONTAINER_REDUCED =
    {
        BOTokens.ML_NAME,
        BOTokens.ML_EMAIL,
    }; // LST_HEADINGS_PERSONCONTAINER_REDUCED

    /**
     * Name of a container column. <BR/>
     * These attributes are used for ordering the elements.
     */
    public static final String [] LST_ORDERINGS_PERSONCONTAINER_REDUCED =
    {
        BOListConstants.LST_ORDERINGS[0],
        MadConstants.ORD_EMAIL,
    }; // LST_ORDERINGS_PERSONCONTAINER_REDUCED


    /**************************************************************************
     * This constructor creates a new instance of the class PersonContainer_01.
     * <BR/>
     */
    public PersonContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // PersonContainer_01


    /**************************************************************************
     * Creates a PersonContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PersonContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // PersonContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();

        this.viewContent = "v_PersonContainer_01$cont";
        this.elementClassName = "m2.mad.PersonContainerElement_01";
        // set majorContainer true
        this.isMajorContainer = true;
    } // initClassSpecifics


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return new StringBuffer ()
            .append ("SELECT  DISTINCT oid, state, name, typeName, isLink, linkedObjectId,")
                .append (" owner, ownerName, ownerOid, ownerFullname, lastChanged,")
                .append (" isNew, icon, email, description")
            .append (" FROM ").append (this.viewContent)
            .append (" WHERE  containerId = ").append (this.oid.toStringQu ())
            .append (" ")
            .toString ();
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B>. <BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue ("&lt;attribute>");. <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>");. <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     *
     * @param   action      The database connection object.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action, ContainerElement commonObj)
        throws DBError
    {
        PersonContainerElement_01 obj = (PersonContainerElement_01) commonObj;
        super.getContainerElementData (action, obj);
        obj.email = action.getString ("email");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        if (!this.getUserInfo ().userProfile.showExtendedAttributes)
        {
            // reduced list

            // set headings:
            this.headings = MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE, 
                PersonContainer_01.LST_HEADINGS_PERSONCONTAINER_REDUCED, env);

            // set ordering attributes for the corresponding headings:
            this.orderings = PersonContainer_01.LST_ORDERINGS_PERSONCONTAINER_REDUCED;
        } // if
        else
        { // extended headingslist
            super.setHeadingsAndOrderings ();

            // extend the headings
            String[] temp = this.headings;
            String[] temp2 = this.orderings;
            this.headings = new String[this.headings.length - 1];
            this.orderings = new String[this.orderings.length - 1];
            int counter = 0;
            for (int i = 0; i < temp.length; i++)
            {
                // TODO RB: Will this work correctly with MLI support??
                if (!temp[i].equalsIgnoreCase ( 
                    MultilingualTextProvider.getText (MadTokens.TOK_BUNDLE,
                        MadTokens.ML_COMPOWNER, env)))
                {
                    this.headings[counter] = temp[i];
                    this.orderings[counter++] = temp2[i];
                } // if
            } // for
        } // else

        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings

} // class PersonContainer_01

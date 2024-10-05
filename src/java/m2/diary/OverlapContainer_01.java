/*
 * Class: OverlapContainer_01.java
 */

// package:
package m2.diary;

// imports:
import ibs.bo.BOTokens;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.RowElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.DateTimeHelpers;

import m2.diary.DiaryArguments;
import m2.diary.DiaryConstants;
import m2.diary.DiaryFunctions;
import m2.diary.DiaryTokens;
import m2.diary.OverlapContainerElement_01;

import java.util.Date;


/******************************************************************************
 * This class represents one object of the container type OverlapContainer_01.
 * <BR/>
 * It shows all overlapping term objects of a given term. This object has no
 * representation in the database.
 *
 * @version     $Id: OverlapContainer_01.java,v 1.16 2010/04/13 15:55:57 rburgermann Exp $
 *
 * @author      Horst Pichler   (HP), 980428
 ******************************************************************************
 */
public class OverlapContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: OverlapContainer_01.java,v 1.16 2010/04/13 15:55:57 rburgermann Exp $";


    /**
     * Termin_01 object: used to find overlaps. <BR/>
     */
    protected OID termOID;

    /**
     * startDate
     */
    protected Date startDateTime;

    /**
     * endDate
     */
    protected Date endDateTime;


    /**************************************************************************
     * This constructor creates a new instance of the class OverlapContainer_01.
     * <BR/>
     */
    public OverlapContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // OverlapContainer_01


    /**************************************************************************
     * Creates a OverlapContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public OverlapContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // OverlapContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set header-display function of container
        this.fct = DiaryFunctions.FCT_TERM_OVERLAP_FRAME_LIST;

        this.viewContent = "v_OverlapContainer$content";
        this.elementClassName = "m2.diary.OverlapContainerElement_01";

        // this object type is never persistent
        this.isPhysical = false;
        this.name = 
            MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE,
                DiaryTokens.ML_OVERLAPS, env);
        this.displayButtons = false;
        this.setIcon ();
    } // initClassSpecifics


    /**************************************************************************
     * Sets the term for which the overlap search is performed. <BR/>
     *
     * @param   term    Value for the term object id.
     */
    public void setOverlapTerm (OID term)
    {
        // set OID for term (which will be checked for overlaps)
        this.termOID = term;

        this.startDateTime = this.env.getDateTimeParam (DiaryArguments.ARG_TERM_START_DATE);
        this.endDateTime = this.env.getDateTimeParam (DiaryArguments.ARG_TERM_END_DATE);
    } // setOverlapTerm


    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        StringBuffer queryStr;          // the query string

        queryStr = new StringBuffer ()
            .append (" SELECT DISTINCT *")
            .append (" FROM ").append (this.viewContent)
            // do not check term with term itself
            .append (" WHERE  oid <> ").append (this.termOID.toStringQu ())
            // check overlapping terms
            .append ("    AND startDate < ")
            .append (SQLHelpers.getDateString (DateTimeHelpers.dateTimeToString (this.endDateTime)))
            .append ("    AND endDate > ")
            .append (SQLHelpers.getDateString (DateTimeHelpers.dateTimeToString (this.startDateTime)));

        // return the constructed query:
        return queryStr.toString ();
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
     *      obj.%lt;property> = getQuOidValue (action, "&lt;attribute>");. <BR/>
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
        super.getContainerElementData (action, commonObj);
        OverlapContainerElement_01 obj = (OverlapContainerElement_01) commonObj;

        obj.startDate = action.getDate ("startDate");
        obj.endDate = action.getDate ("endDate");
        obj.place = action.getString ("place");
    } // getContainerElementData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard container. <BR/>
     */
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (DiaryTokens.TOK_BUNDLE, 
            DiaryConstants.LST_HEADINGS_OVERLAP, env);
        // set super attribute
        this.orderings = DiaryConstants.LST_ORDERINGS_OVERLAP;
        // ensure that there is an available ordering taken:
        this.ensureAvailableOrdering ();
    } // setHeadingsAndOrderings


    /**************************************************************************
     * Create the heading of a list. <BR/>
     * The property <A HREF="#orderings">orderings</A> is queried to determine
     * if a column shall be orderable or not. If orderings[i] == null the column
     * i is not orderable.
     *
     * @param   headings    Headings to be shown.
     * @param   orderings   Corresponding orderings for the headings.
     * @param   orderBy     Property, by which the result is sorted.
     * @param   orderHow    Kind of ordering:
     *                      {@link ibs.bo.BOConstants#ORDER_ASC BOConstants.ORDER_ASC} or
     *                      {@link ibs.bo.BOConstants#ORDER_DESC BOConstants.ORDER_DESC}
     *                      <CODE>null</CODE> =>
     *                      {@link ibs.bo.BOConstants#ORDER_ASC BOConstants.ORDER_ASC}
     * @param   isOrderingsChangeable Shall the orderings be changeable?
     *
     * @return  Table row containing the heading of the list.
     */
    protected RowElement createHeading (String [] headings,
        String [] orderings, int orderBy, String orderHow,
        boolean isOrderingsChangeable)
    {
        return super.createHeading (headings, orderings, orderBy, orderHow, false);
    } // createHeading

} // class OverlapContainer_01

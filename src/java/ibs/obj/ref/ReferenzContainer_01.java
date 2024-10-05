/*
 * Class: ReferenzContainer_01.java
 */

// package:
package ibs.obj.ref;

// imports:
//KR TODO: unsauber
import ibs.app.AppFunctions;
import ibs.app.CssConstants;
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.bo.type.Type;
import ibs.di.DocumentTemplate_01;
import ibs.di.ValueDataElement;
import ibs.di.ValueDataElementTS;
import ibs.io.HtmlConstants;
import ibs.io.IOConstants;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.DivElement;
import ibs.tech.html.Element;
import ibs.tech.html.GroupElement;
import ibs.tech.html.NewLineElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TableElement;
import ibs.tech.html.TextElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.util.NoAccessException;

import java.util.Iterator;
import java.util.Vector;


/******************************************************************************
 * This class represents one object of type Dokument with version 01. <BR/>
 *
 * @version     $Id: ReferenzContainer_01.java,v 1.17 2013/01/23 09:44:44 rburgermann Exp $
 *
 * @author      Klaus Reimüller (KR), 980428
 ******************************************************************************
 */
public class ReferenzContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ReferenzContainer_01.java,v 1.17 2013/01/23 09:44:44 rburgermann Exp $";

    /**
     * Holds a specific link type to switch to special retrieve and show handling.
     */
    private Type p_linkType = null;

    /**
     * Vector that holds possible addtional types. <BR/>
     */
    protected Vector<Type> additionalTypes = null;

    /**
     * int which holds the current operation. <BR/>
     */
    private int currentOperation = Operations.OP_VIEW;

    /**************************************************************************
     * This constructor creates a new instance of the class ReferenzContainer_01.
     * <BR/>
     */
    public ReferenzContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // ReferenzContainer_01


    /**************************************************************************
     * Creates a ReferenzContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ReferenzContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // ReferenzContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        this.elementClassName = "ibs.obj.ref.ReferenzContainerElement_01";
    } // initClassSpecifics


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation         Operation to be performed with the objects.
     * @param   orderBy           Property, by which the result shall be
     *                            sorted. If this parameter is null the
     *                            default order is by name.
     * @param   orderHow          Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                            null => BOConstants.ORDER_ASC
     * @param   selectedElements  object ids that are marked for paste
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow, Vector<OID> selectedElements)
    throws NoAccessException
    {
        // store the operation for specific use within this reference container
        this.currentOperation = operation; 
        // call the super method to handle the retrieve of data
        super.performRetrieveContentData (operation, orderBy, orderHow, selectedElements);
    } // performRetrieveContentData

    /**************************************************************************
     * Method to add addtional data after the default data of the page. <BR/>
     * This is a stub method which should be overridden in subclasses. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is sorted.
     * @param   orderHow            Kind of ordering:
     *                              BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    protected Element performShowSpecificContent (int representationForm,
                                                  int orderBy, String orderHow)
    {
        DivElement specificContentElement = null;
        
        // exists any additional type which must be handled separately? 
        if (this.additionalTypes != null)
        {
            // iterate over all additional types and handle them separately
            Iterator<Type> additionalTypesIter = this.additionalTypes.iterator();
    
            // if additional types are available, create a new surrounding element 
            if (additionalTypesIter.hasNext ())
            {
                specificContentElement = new DivElement ();
            } // if
       
            // iterate over all additional types and handle them separately
            while(additionalTypesIter.hasNext ())
            {
                Type oneAdditionalType = additionalTypesIter.next ();
                specificContentElement.addElement (
                    performShowTypeSpecificContent (oneAdditionalType, representationForm,
                                                    orderBy, orderHow));
            } // while
        } // if
        
        return specificContentElement;
    } // performShowSpecificContent


    /**************************************************************************
     * Get the additional data for a special link field from the database. <BR/>
     *
     * @param   objectType          Type of the addtional objects.
     * @param   representationForm  Kind of representation.
     * @param   orderBy             Property, by which the result is sorted.
     * @param   orderHow            Kind of ordering:
     *                              BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                              null => BOConstants.ORDER_ASC
     */
    protected Element performShowTypeSpecificContent (Type singleType, int representationForm,
                                                      int orderBy, String orderHow)
    {
        Element additionalDataElement = null;
        
        try 
        {
            // Set the p_linkType variable to use special retrieval handling
            this.p_linkType = singleType;

            // get the additional data for the specific link fields out of the database            
            performRetrieveContentData (Operations.OP_VIEW, orderBy, orderHow, null);

            // Display the data of the additional type within a separate table
            additionalDataElement = performShowTypeSpecificContentData();

            // Reset the p_linkType variable to avoid special handling
            this.p_linkType = null;
        } // try
        catch (NoAccessException ex)
        {
            // send message to the user:
            this.showNoAccessMessage (Operations.OP_VIEWELEMS);
        } // NoAccessException
        
        return additionalDataElement;
    } // performShowTypeSpecificContent

    
    /**************************************************************************
     * Show the additional type specific content within a separate table. <BR/>
     * This method handles the complete display of the table to display the
     * type specific content. <BR/><BR/>
     * 
     * @return  Element     Contains an Element with the table of the type
     *                      specific data
     */
    protected Element performShowTypeSpecificContentData ()
    {
        int i;                          // actual index
        GroupElement specificContent = new GroupElement ();
        // save the original headings and orderings to avoid duplication of columns
        String [] originalHeadings = this.headings;
        String [] originalOrderings = this.orderings;

        // set the icon of this object
        this.setIcon ();

        // show type specific description for the table
        specificContent.addElement (new NewLineElement ());
        specificContent.addElement (createTypeSpecificDescription ());

        TableElement table;     // table containing the list
        RowElement tr;          // actual table row

        // set the type specific headings and orderings
        this.headings = setTypeSpecificHeadings(this.headings);
        this.orderings = setTypeSpecificOrderings(super.orderings);

        // set number of colums in result list:
        int columns = this.headings.length + 1;

        // set alignments:
        String[] alignments = new String[columns - 1];
        alignments[0] = "" + IOConstants.ALIGN_RIGHT + "\">" +
            "<COL CLASS=\"" + CssConstants.CLASS_NAME + "\"" +
            " ALIGN=\"" + IOConstants.ALIGN_LEFT;
        // set alignments for all columns:
        for (i = 1; i < columns - 1; i++)
        {
            alignments[i] = null;
        } // for i

        // create table definition for list
        table = new TableElement (columns);
        table.classId = CssConstants.CLASS_LIST;
        table.frametypes = IOConstants.FRAME_VOID;
        table.width = HtmlConstants.TAV_FULLWIDTH;
        table.alignment = alignments;
    
        // create the heading for the type specific table
        tr = super.createHeading (this.headings, this.orderings, 
            BOListConstants.LST_DEFAULTORDERING, BOConstants.ORDER_ASC, false);
    
        // add header to table
        table.addElement (tr, true);
        
        // loop through all elements of this container and display them.
        Iterator<ContainerElement> elementIterator = this.elements.iterator ();
        int k = 0;      // variable to display correct row style
        while (elementIterator.hasNext ())
        {
            ReferenzContainerElement_01 oneElem = (ReferenzContainerElement_01) elementIterator.next ();
            oneElem.showExtendedAttributes = this.getUserInfo ().userProfile.showExtendedAttributes;
            table.addElement (oneElem.show (BOListConstants.LST_CLASSROWS[k++ % BOListConstants.LST_CLASSROWS.length], env));
        } // while
    
        // add the table to the specific content
        specificContent.addElement (table);
        
        // set headings and orderings back to original value for next possible iteration
        this.headings = originalHeadings;
        this.orderings = originalOrderings;
        
        return specificContent;
    } // performShowTypeSpecificContent
    
    
    /**************************************************************************
     * Add the headings for the additional data fields. <BR/>
     *
     * @param   typeSpecificHeadings    String []   Headings with default fields
     *                                              to which the additional data
     *                                              fields should be added.
     * @return  String []   Contains the headings for the default fields AND
     *                      the additional fields.
     */    
    protected String[] setTypeSpecificHeadings(String [] typeSpecificHeadings)
    {
        if (this.elements != null && !this.elements.isEmpty ())
        {
            // get the first element to identify the headings for this table
            ReferenzContainerElement_01 oneElem = (ReferenzContainerElement_01) this.elements.firstElement ();
            
            // local variables to store the additional headings
            String [] typeSpecificHeadingsExtended = new String [typeSpecificHeadings.length + oneElem.values.size ()];
    
            // copy the default values to the new arrays 
            System.arraycopy (typeSpecificHeadings, 0, typeSpecificHeadingsExtended, 0, typeSpecificHeadings.length);
            
            int nextPosition = typeSpecificHeadings.length;
            // iterate over all possible additional fields and add the retrieved multilingual name
            Iterator<ValueDataElement> valuesIterator = oneElem.values.iterator ();
            while (valuesIterator.hasNext ())
            {
                ValueDataElement oneValue = valuesIterator.next ();
                // Add the multilingual name for this field
                typeSpecificHeadingsExtended[nextPosition] = oneValue.getMlInfo (env).getName ();
                // increase to add to the nextPosition
                nextPosition++;
            } // while
    
            // return the extended array with the additional fields    
            return typeSpecificHeadingsExtended;
        } // if
        else
        {
            return typeSpecificHeadings;
        } // else
    } // setTypeSpecificHeadings
    

    /**************************************************************************
     * Add the orderings for the additional data fields. <BR/>
     *
     * @param   typeSpecificOrderings    String []  Orderings with default fields
     *                                              to which the additional data
     *                                              fields should be added.
     * @return  String []   Contains the orderings for the default fields AND
     *                      the additional fields.
     */    
    protected String[] setTypeSpecificOrderings (String [] typeSpecificOrderings)
    {
        if (this.elements != null && !this.elements.isEmpty ())
        {
            // get the first element to identify the orderings for this table
            ReferenzContainerElement_01 oneElem = (ReferenzContainerElement_01) this.elements.firstElement ();
            
            // local variables to store the additional orderings
            String [] typeSpecificOrderingsExtended = new String [typeSpecificOrderings.length + oneElem.values.size ()];
    
            // copy the default values to the new arrays 
            System.arraycopy (typeSpecificOrderings, 0, typeSpecificOrderingsExtended, 0, typeSpecificOrderings.length);
            
            int nextPosition = typeSpecificOrderings.length;
            // iterate over all possible additional fields
            Iterator<ValueDataElement> valuesIterator = oneElem.values.iterator ();
            while (valuesIterator.hasNext ())
            {
                ValueDataElement oneValue = valuesIterator.next ();
                // Add the ordering name for this field
                typeSpecificOrderingsExtended[nextPosition] = oneValue.mappingField;
                // increase to add to the nextPosition
                nextPosition++;
            } // while
    
            // return the extended array with the additional fields    
            return typeSpecificOrderingsExtended;
        } // if
        else
        {
            return typeSpecificOrderings;
        } // else
    } // setTypeSpecificOrderings

    
    /**************************************************************************
     * Creates the type specific description of a list. <BR/>
     *
     * @return  Group element containing the graphical representation of the
     *          type specific description.
     */
    protected GroupElement createTypeSpecificDescription ()
    {
        TableElement table = null;      // table containing the description
        RowElement tr;                  // actual table row
        TableDataElement td;            // actual element of a row
        GroupElement group = new GroupElement ();

        if (this.elements != null && !this.elements.isEmpty ())
        {
            // get the first element to identify the orderings for this table
            ReferenzContainerElement_01 oneElem = (ReferenzContainerElement_01) this.elements.firstElement ();
    
            if (oneElem.typeName != null)
            {
                // set typeName as default name
                String typeMlName = oneElem.typeName;
                
                // get the type for this template and try to receive the multilingual name
                Type type = this.getTypeCache ().findTypeByName(oneElem.typeName);
                // check if the type was found
                if (type != null)
                {
                    // get multilingual name for this type
                    typeMlName = type.getMlName (MultilingualTextProvider.getUserLocale (env).getLocale ());
                } // if
    
                // set the title of the type specific table
                String typeTableDescription = MultilingualTextProvider.getText (
                    BOTokens.TOK_BUNDLE, BOTokens.ML_LINKEDOBJECTS, env) + " " + typeMlName;
                
                table = new TableElement (1); // create table
                table.width = HtmlConstants.TAV_FULLWIDTH;       // set width of table to full frame size
                table.border = 0;
                table.classId = CssConstants.CLASS_LISTDESCRIPTION;
                tr = new RowElement (1);    // create table row
                tr.classId = CssConstants.CLASS_LISTDESCRIPTION;
                td = new TableDataElement (new TextElement (typeTableDescription));
                td.classId = CssConstants.CLASS_LISTDESCRIPTION;
    
                // add text to table and add table to body of document:
                tr.addElement (td);
                table.addElement (tr);
                group.addElement (table);
            } // if description exists
         } // if
        
        return group;                   // return the constructed group element
    } // createTypeSpecificDescription

    
    /**************************************************************************
     * Create the query to get the container's content out of the database.
     * <BR/>
     * The query or view must at least have the attributes userId and rights.
     * Queries on these attributes have to be addable to this query. <BR/>
     * The query must have at least one constraint within WHERE to ensure that
     * other constraints can be added with AND. <BR/>
     * <B>Standard format:</B><BR/>
     *      "SELECT DISTINCT oid, &lt;other attributes> " +
     *      " FROM " + this.viewContent +
     *      " WHERE containerId = " + oid;<BR/>
     * This method can be overwritten in subclasses. <BR/>
     * As you can see the query should contain at least the oid.
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        // check if additional type data should be retrieved
        if (p_linkType != null)
        {
            StringBuilder queryStr = new StringBuilder ();
            // get the query to retrieve the link field data
            queryStr.append (p_linkType.getTemplate ().getLinkFieldQuery ());
            // add the additional clause to retrieve only the necessary data
            queryStr.append (" AND ibs_object.containerId = ").append (this.oid.toStringQu ());
            
            return queryStr.toString ();
        } // if
        // use default handling to retrieve data
        else
        {
            if (this.isTab ())
            {
                return new StringBuilder ()
                    .append (" SELECT DISTINCT linkType, oid, state, name, typeCode, typeName, isLink,")
                    .append ("         linkedObjectId, owner, ownerName, ownerOid,")
                    .append ("         ownerFullname, lastChanged, isNew, icon, description,")
                    .append ("         flags, processState")
                    .append (" FROM    v_RefContainer_01$content")
                    .append (" WHERE   containerId = ").append (this.oid.toStringQu ())
                    .toString ();
            } // if
        
            return super.createQueryRetrieveContentData ();
        } // else
    } // createQueryRetrieveContentData


    /**************************************************************************
     * Get the element type specific data out of the actual tuple of the query
     * result. <BR/>
     * This metod is used to get all attributes of one element out of the
     * resultset. The attribute names which can be used are the ones which
     * are defined within the resultset of
     * <A HREF="#createQueryRetrieveContentData">createQueryRetrieveContentData</A>.
     * <BR/>
     * <B>Format:</B><BR/>
     * for oid properties:
     *      obj.&lt;property> = getQuOidValue (action, "&lt;attribute>"); <BR/>
     * for other properties:
     *      obj.&lt;property> = action.get&lt;type> ("&lt;attribute>"); <BR/>
     * The property <B>oid</B> is already gotten. This must not be done within this
     * method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @exception   DBError
     *              Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        // If no specific link type is set use the default handling 
        if (p_linkType == null)
        {
            super.getContainerElementData (action, commonObj);
    
            if (this.isTab ())
            {
                ReferenzContainerElement_01 elem =
                    (ReferenzContainerElement_01) commonObj;
    
                // try
                try
                {
                    elem.setLinkType (action.getInt ("linkType"));

                    // Special handling only done when in view mode and NOT IN delete mode
                    if (Operations.OP_DELETE != currentOperation)
                    {
                        // search for the type in the type cache
                        Type type = this.getTypeCache ().findType(elem.typeCode);
                        // check if the type was found
                        if (type != null)
                        {
                            // get the template of the type:
                            DocumentTemplate_01 elemDocTemplate = (DocumentTemplate_01) (type.getTemplate ());
                            // if special link fields are defined, add the type to the additional types
                            if(elemDocTemplate.getLinkFields () != null)
                            {
                                // initialize with an empty vector if additionalTypes is NULL
                                if (this.additionalTypes == null)
                                {
                                    this.additionalTypes = new Vector<Type> ();
                                } // if
                                // only add this type when not in additionalTypes yet
                                if (!this.additionalTypes.contains (type))
                                {
                                    // Add this type to the additional types 
                                    this.additionalTypes.add (type);
                                } // if
                                // Remove this type from the general elements list
                                super.elements.removeElement (commonObj);
                            } // if
                        } // if
                    } // if
                } // try
                catch (DBError e)
                {
                    // this error was thrown when executing multiple insert
                    // in reference container, because v_Container$content
                    // was used as multiple-insert view
                    // - the problem is that v_RefContainer$content
                    // can't be used as multiple-insert view otherwise
                    // it would be set in this.viewContent
                } // catch
            } // if
        } // if
        // Get data for defined fields for the specific type
        else
        {
            if (this.isTab ())
            {
                ReferenzContainerElement_01 elem =
                    (ReferenzContainerElement_01) commonObj;

                elem.setLinkType (action.getInt ("linkType"));

                // get default values from the result set for this element
                elem.state = action.getInt ("state");
                elem.name = action.getString ("name");
                elem.typeCode = action.getString ("typeCode");
                elem.typeName = action.getString ("typeName");
                elem.isLink = action.getBoolean ("isLink");
                elem.linkedObjectId = SQLHelpers.getQuOidValue (action, "linkedObjectId");
                elem.owner = new User (action.getInt ("owner"));
                elem.owner.username = action.getString ("ownerName");
                elem.owner.oid = SQLHelpers.getQuOidValue (action, "ownerOid");
                elem.owner.fullname = action.getString ("ownerFullname");
                elem.lastChanged = action.getDate ("lastChanged");
                elem.icon = action.getString ("icon");
                elem.description = action.getString ("description");

                if ((this.sess != null) && (this.sess.activeLayout != null))
                {
                    elem.layoutpath = this.sess.activeLayout.path;
                } // if
                else
                {
                    elem.layoutpath = "";
                } // else

                // get the additional data out of the query result
                elem.values = ValueDataElementTS.getAllDBValues (
                    this.p_linkType.getTemplate ().getLinkFields(), action, env);
            } // if
        } // else
    } // getContainerElementData


    /**************************************************************************
     * Show the elements that may be added as link to the Container.
     * The objects are gotten from the memory and
     * represented to the user in the required form. <BR/>
     *
     * @param   representationForm  Kind of representation.
     * @param   selectedElements    object ids that are marked for paste
     */
    public void showPasteLinkSelForm (int representationForm,
                                      Vector<OID> selectedElements)
    {
        this.showSelectionForm (representationForm,
            AppFunctions.FCT_LISTLINKPASTEFORM, Operations.OP_COPY,
            AppFunctions.FCT_LISTPASTELINK, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_SELHEADERPASTELINK, env),
            selectedElements);
    } // showPasteLinkSelForm


    /**************************************************************************
     * Check if an object of a specific type can be inserted within this
     * object. <BR/>
     * The parameter should be a valid tVersionId. Currently this method also
     * works if this is just a type id.
     *
     * @param   type    Type of the object which shall be inserted.
     *
     * @return true if the object is allowed to be inserted, false otherwise.
     */
    public boolean isAllowedType (int type)
    {
        // for a reference container all other types are allowed to be inserted
        // (of course as links):
        return true;
    } // isAllowedType


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in an
     * object's info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setInfoButtons ()
    {
        // define buttons to be displayed:
        int [] buttons =
        {
            Buttons.BTN_EDIT,
// Buttons.BTN_DELETE,
// Buttons.BTN_CUT,
            Buttons.BTN_COPY,
            Buttons.BTN_DISTRIBUTE,
            Buttons.BTN_SEARCH,
// Buttons.BTN_HELP,
        }; // buttons

        // return button array
        return buttons;
    } // showInfoButtons


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * container's content view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
     *
     * @return  An array with button ids that can potentially be displayed.
     */
    protected int[] setContentButtons ()
    {
        // define buttons to be displayed:
        int[] buttons =
        {
//    Buttons.BTN_NEW,
//    Buttons.BTN_PASTE,
            Buttons.BTN_SEARCH,
//         Buttons.BTN_HELP,
            Buttons.BTN_LISTDELETE,
            Buttons.BTN_REFERENCE,
        }; // buttons

        // return button array
        return buttons;
    } // setContentButtons


    /**************************************************************************
     * Extend the query with constraints specific for delete operations. <BR/>
     * In this case make sure that only physical links can be deleted.
     *
     * @return  The extension to the query.
     *
     * @see ibs.bo.Container#extendQueryRetrieveDeleteData
     */
    protected StringBuffer extendQueryRetrieveDeleteData ()
    {
        return super.extendQueryRetrieveDeleteData ()
            // get the oid of all referenced document templates
            .append (" AND linkType = 1");
    } // extendQueryRetrieveDeleteData


    /**************************************************************************
     * This function returns the number of elements in this container. <BR/>
     * Its used in the BuliButtonBar function of the BusinessObject and used,BR>
     * for disabling buttons for list operation , if there are no elements in the
     * container.
     *
     * @return  The no of elements in the container if elements is defined otherwise
     *          returns <CODE>0</CODE>.
     */
    protected int getElementSize ()
    {
        int combinedSize = 0;
        
        // do we have an element
        if (this.elements != null)
        {
            combinedSize = this.elements.size ();
        } // if
        // do we have additional types?
        if (this.additionalTypes != null)
        {
            combinedSize += this.additionalTypes.size ();
        } // if

        return combinedSize;
    } // getElementSize
    
} // class ReferenzContainer_01

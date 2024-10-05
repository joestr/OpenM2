/*
 * Class: XMLDiscussionTemplate_01.java
 */

// package:
package m2.bbd;

// imports:
import ibs.bo.BOHelpers;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.Operations;
import ibs.di.DataElement;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.GroupElement;
import ibs.tech.html.SelectElement;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.Parameter;
import ibs.tech.sql.ParameterConstants;
import ibs.tech.sql.SQLAction;
import ibs.tech.sql.SQLHelpers;
import ibs.tech.sql.StoredProcedure;

import m2.bbd.BbdArguments;
import m2.bbd.BbdTokens;

import java.util.Vector;


/******************************************************************************
 * This class represents one object of type XMLDiscussionTemplate with version 01.
 * A XMLDiscussionTemplate object holds an XML file that serves as a template for new
 * document types. DocumentTemplates act like file objects that store single
 * files. XMLViewer objects read these XML files to build their document structure
 * upon the definiton in the XML file. <BR/>
 *
 * @version     $Id: XMLDiscussionTemplate_01.java,v 1.15 2013/01/16 16:14:10 btatzmann Exp $
 *
 * @author      Christine Keim (CK) 000926
 ******************************************************************************
 */
public class XMLDiscussionTemplate_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLDiscussionTemplate_01.java,v 1.15 2013/01/16 16:14:10 btatzmann Exp $";


    /**
     *  first level XMLTemplate
     */
    public OID level1 = null;

    /**
     *  first level XMLTemplate-name
     */
    public String level1Name = "";

    /**
     *  second level XMLTemplate
     */
    public OID level2 = null;

    /**
     *  second level XMLTemplate-name
     */
    public String level2Name = "";

    /**
     *  third level XMLTemplate
     */
    public OID level3 = null;

    /**
     *  third level XMLTemplate-name
     */
    public String level3Name = "";

    /**
     *  Vector with all templateOid/templateName pairs
     */
    protected Vector<String[]> templates = null;

    /**
     * The number of objects where this template is used. <BR/>
     */
    public int numberOfReferences = 0;

    /**
     * Query column name for oid.
     */
    private static final String QCN_OID = "oid";


    /**************************************************************************
     * This constructor creates a new instance of the class XMLDiscussionTemplate_01.
     * <BR/>
     */
    public XMLDiscussionTemplate_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussionTemplate_01


    /**************************************************************************
     * Creates a XMLDiscussionTemplate_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public XMLDiscussionTemplate_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);

        // initialize properties common to all subclasses:

        // init specifics of actual class:
    } // XMLDiscussionTemplate_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        super.initClassSpecifics ();    // has same specifics as super class

        // set the class-procedureNames
        this.procCreate = "p_XMLDiscTemplate_01$create";
        this.procChange = "p_XMLDiscTemplate_01$change";
        this.procRetrieve = "p_XMLDiscTemplate_01$retrieve";
        this.procDelete = "p_XMLDiscTemplate_01$delete";
        this.procDeleteRec = this.procDelete;

        this.specificChangeParameters = 3;
        this.specificRetrieveParameters = 7;
    } // initClassSpecifics


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performChangeData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the change data stored procedure.
     *
     * @param sp        The stored procedure to add the change parameters to.
     */
    @Override
    protected void setSpecificChangeParameters (StoredProcedure sp)
    {
        // set the specific parameters:
        // level 1
        BOHelpers.addInParameter (sp, this.level1);

        // level 2
        BOHelpers.addInParameter (sp, this.level2);

        // level 3
        BOHelpers.addInParameter (sp, this.level3);
    } // setSpecificChangeParameters


    /**************************************************************************
     * Set the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to pass
     * type specific data to the retrieve data stored procedure.
     *
     * @param sp        The stored procedure the specific retrieve parameters
     *                  should be added to.
     * @param params    Array of parameters the specific retrieve parameters
     *                  have to be added to for beeing able to retrieve the
     *                  results within getSpecificRetrieveParameters.
     * @param lastIndex The index to the last element used in params thus far.
     *
     * @return  The index of the last element used in params.
     */
    @Override
    protected int setSpecificRetrieveParameters (StoredProcedure sp, Parameter[] params,
                                                 int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // set the specific parameters:
        // level 1
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // level 1 name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // level 2
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // level 2 name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // level 3
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_VARBYTE);
        // level 3 name
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_STRING);

        // numberOfReferences
        params[++i] = sp.addOutParameter (ParameterConstants.TYPE_INTEGER);

        return i;                       // return the current index
    } // setSpecificRetrieveParameters


    /**************************************************************************
     * Get the data for the additional (type specific) parameters for
     * performRetrieveData. <BR/>
     * This method must be overwritten by all subclasses that have to get
     * type specific data from the retrieve data stored procedure.
     *
     * @param   params      The array of parameters from the retrieve data
     *                      stored procedure.
     * @param   lastIndex   The index to the last element used in params thus
     *                      far.
     */
    protected void getSpecificRetrieveParameters (Parameter[] params,
                                                  int lastIndex)
    {
        int i = lastIndex;              // initialize params index

        // get the specific parameters:
        this.level1 = SQLHelpers.getSpOidParam (params[++i]);
        this.level1Name = params[++i].getValueString ();
        this.level2 = SQLHelpers.getSpOidParam (params[++i]);
        this.level2Name = params[++i].getValueString ();
        this.level3 = SQLHelpers.getSpOidParam (params[++i]);
        this.level3Name = params[++i].getValueString ();
        this.numberOfReferences = params[++i].getValueInteger ();
    } // getSpecificRetrieveParameters


    /**************************************************************************
     * Read form the User the data used in the Object. <BR/>
     */
    public void getParameters ()
    {
        OID temp = null;

        super.getParameters ();

        // get the oids of the templates
        // get oid of level 1:
        if ((temp = this.env.getOidParam (BbdArguments.ARG_LEVEL1)) != null)
        {
            this.level1 = temp;
        } // if

        // get oid of level 2:
        if ((temp = this.env.getOidParam (BbdArguments.ARG_LEVEL2)) != null)
        {
            this.level2 = temp;
        } // if

        // get oid of level 3:
        if ((temp = this.env.getOidParam (BbdArguments.ARG_LEVEL3)) != null)
        {
            this.level3 = temp;
        } // if
    } // getParameters


    /**************************************************************************
     * Represent the properties of a DocumentTemplate_01 object to the user.
     * <BR/>
     *
     * @param   table       Table where the properties should be added.
     *
     * @see ibs.IbsObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        super.showProperties (table);

        // show the names of the templates for the levels
        this.showProperty (table, BbdArguments.ARG_DUMMY,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL1, env),
            Datatypes.DT_NAME, this.level1Name);
        this.showProperty (table, BbdArguments.ARG_LEVEL1,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL1, env),
            Datatypes.DT_HIDDEN, this.level1.toString ());
        this.showProperty (table, BbdArguments.ARG_DUMMY,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL2, env),
            Datatypes.DT_NAME, this.level2Name);
        this.showProperty (table, BbdArguments.ARG_LEVEL2,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL2, env),
            Datatypes.DT_HIDDEN, this.level2.toString ());
        this.showProperty (table, BbdArguments.ARG_DUMMY,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL3, env),
            Datatypes.DT_NAME, this.level3Name);
        this.showProperty (table, BbdArguments.ARG_LEVEL3,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL3, env),
            Datatypes.DT_HIDDEN, this.level3.toString ());
    } //  showProperties


    /**************************************************************************
     * Represent the properties of a XMLDiscussionTemplate_01 object to the user
     * within a form. <BR/>
     *
     * @param   table   Table where the properties shall be added.
     *
     * @see ibs.IbsObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        GroupElement gel;    // holds a selectionbox
        SelectElement sel;

        // loop through all properties of this object and display them:
        super.showFormProperties (table);

        // get the Vector with the names and the oids of the DiscussionTemplates in the system
        if (this.templates == null)
        {
            this.templates = this.createTemplatesVector ();
        } // if

        gel = new GroupElement ();
        sel = this.createSelectionBox (BbdArguments.ARG_LEVEL1, this.templates, this.level1.toString ());
        sel.size = 1;
        sel.multiple = false;
        // show level one
        gel.addElement (sel);
        this.showFormProperty (table,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL1, env), gel);

        gel = new GroupElement ();
        sel = this.createSelectionBox (BbdArguments.ARG_LEVEL2, this.templates, this.level2.toString ());
        sel.size = 1;
        sel.multiple = false;
        // show level two
        gel.addElement (sel);
        this.showFormProperty (table,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL2, env), gel);

        gel = new GroupElement ();
        // show level three
        sel = this.createSelectionBox (BbdArguments.ARG_LEVEL3, this.templates, this.level3.toString ());
        sel.size = 1;
        sel.multiple = false;
        // show level three
        gel.addElement (sel);
        this.showFormProperty (table,  
            MultilingualTextProvider.getText (BbdTokens.TOK_BUNDLE,
                BbdTokens.ML_LEVEL3, env), gel);
    } // showFormProperties


    /**************************************************************************
     * Create a Vector with the templates within a form. <BR/>
     *
     * @return  The Vector filled with the templates
     */
    protected Vector<String[]> createTemplatesVector ()
    {
        Vector<String[]> temp = new Vector<String[]> ();
        SQLAction action = null;

        // set view to use for the query
        String viewContent = "v_Container$content";

        // init variables
        int rowCount;

        String queryStr =
            " SELECT DISTINCT o.oid AS " + XMLDiscussionTemplate_01.QCN_OID + ", o.name" +
            " FROM " + viewContent + " o, ibs_DocumentTemplate_01 t" +
            " WHERE o.userId = " + this.user.id +
            SQLHelpers.getStringCheckRights (Operations.OP_VIEW) +
            " AND o.oid = t.oid " +
            " AND t.objectSuperType = 'DiscXMLViewer'" +
            " ORDER BY o.name";

        // open db connection
        action = this.getDBConnection ();

        // execute the queryString, indicate that we're not performing an action query:
        try
        {
            rowCount = action.execute (queryStr, false);

            // empty resultSet?
            if (rowCount == 0)
            {
                return temp;
            } // if
            // error while executing
            else if (rowCount < 0)
            {
                return temp;
            } // else if

            // get tuples out of db
            while (!action.getEOF ())
            {
                // create entries in list
                String[] t = new String [2];
                t[0] = action.getString ("Name");
                try
                {
                    OID t2 = new OID (action.getString (XMLDiscussionTemplate_01.QCN_OID));
                    t[1] = t2.toString ();
                } // try
                catch (IncorrectOidException e)
                {
                    t[1] = action.getString (XMLDiscussionTemplate_01.QCN_OID);
                } // catch
                temp.addElement (t);
                // step one tuple ahead for the next loop
                action.next ();
            } // while

            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround -
            // db connection must be handled somewhere else
            this.releaseDBConnection (action);
        } // finally

        return temp;
    } // createTemplatesVector


    //
    // IMPORT / EXPORT METHODS
    //
    /**************************************************************************
     * Writes the object data to an dataElement. <BR/>
     *
     * @param dataElement   The dataElement to write the data to.
     */
    public void writeExportData (DataElement dataElement)
    {
        // set the business object specific values
        super.writeExportData (dataElement);
        // set the type specific values
        dataElement.setExportValue ("level1", this.level1.toString ());
        dataElement.setExportValue ("level2", this.level2.toString ());
        dataElement.setExportValue ("level3", this.level3.toString ());
    } // writeExportData


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
        int [] buttons;                 // define buttons to be displayed

        if (this.numberOfReferences <= 0) // the template is never used?
        {
            // add the buttons DELETE and CUT
            buttons = new int[]
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DELETE,
                Buttons.BTN_CUT,
                Buttons.BTN_DISTRIBUTE,
            }; // buttons
        } // if the template is never used
        else                            // the template can not be deleted
                                        // because there are objects which
                                        // using this template.
        {
            // don't add the buttons DELETE and CUT
            buttons = new int[]
            {
                Buttons.BTN_EDIT,
                Buttons.BTN_DISTRIBUTE,
            }; // buttons
        } // else the template can not be deleted...

        // return button array
        return buttons;
    } // setInfoButtons

} // class XMLDiscussionTemplate_01

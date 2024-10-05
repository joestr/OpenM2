/*
 * Class: ProductProfile_01.java
 */

// package:
package m2.store;

// imports:
import ibs.bo.BOArguments;
import ibs.bo.BOTokens;
import ibs.bo.BusinessObject;
import ibs.bo.Buttons;
import ibs.bo.Datatypes;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.SelectionList;
import ibs.bo.States;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.tech.html.IE302;
import ibs.tech.html.TableElement;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;

import m2.store.StoreArguments;
import m2.store.StoreTokens;
import m2.store.StoreTypeConstants;

import java.util.Vector;


/******************************************************************************
 * This class represents one BusinessObject of type Properties with version 01.
 * <BR/>
 *
 * @version     $Id: ProductProfile_01.java,v 1.15 2010/04/07 13:37:07 rburgermann Exp $
 *
 * @author      Rupert Thurner (RT), 981210
 ******************************************************************************
 */
public class ProductProfile_01 extends BusinessObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ProductProfile_01.java,v 1.15 2010/04/07 13:37:07 rburgermann Exp $";


    /**
     * A string of property categories. <BR/>
     */
    private String[] categories = null;
    /**
     * A string of property categories. <BR/>
     */
    private String[] categoriesOids = null;


    /**************************************************************************
     * Creates a Properties_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     * <BR/>
     */
    public ProductProfile_01 ()
    {
        // call constructor of super class:
        super ();
    } // Properties_01


    /**************************************************************************
     * This constructor creates a new instance of the class Properties_01.
     * <BR/>
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public ProductProfile_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // Properties_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // initialize the instance's private properties:
        this.procCreate =     "p_ProductProfile_01$create";
        this.procChange =     "p_ProductProfile_01$change";
        this.procRetrieve =   "p_ProductProfile_01$retrieve";
        this.procDelete =     "p_ProductProfile_01$delete";
    } // initClassSpecifics


    /**************************************************************************
     * Set the icon of the actual business object. <BR/>
     * If the icon is already set this method leaves it as is.
     * If there is no icon defined yet, the icon name is derived from the name
     * of the type of this object. <BR/>
     */
    protected void setIcon ()
    {
        this.icon = "ProductProfile.gif";
    } // setIcon


    /**************************************************************************
     * Show the properties of a Properties_01 object to the user. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showProperty
     */
    protected void showProperties (TableElement table)
    {
        this.showProperty (table
                        , BOArguments.ARG_NAME
                        , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env)
                        , Datatypes.DT_NAME
                        , this.name);
        // display the object in the news
        this.showProperty (table, BOArguments.ARG_INNEWS, MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env),
            Datatypes.DT_BOOL, "" + this.showInNews);
        // show object specific attributes
        StringBuffer sb = new StringBuffer (50);
        if (this.categories != null)
        {
            for (int i = 0; i < this.categories.length; i++)
            {
                sb.append (this.categories[i]);
                sb.append (IE302.TAG_NEWLINE);
            } // for
        } // if
        else
        {
            sb.append ("");
        } // else

        this.showProperty (table
                        , StoreArguments.ARG_CODECATEGORIES
                        , MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                              StoreTokens.ML_CODECATEGORIES, env)
                        , Datatypes.DT_DESCRIPTION
                        , sb.toString ());
    } // showProperties


    /**************************************************************************
     * Represent the properties of a Properties_01 object to the user
     * within a form. <BR/>
     *
     * @param   table       Table where the properties shall be added.
     *
     * @see ibs.bo.BusinessObject#showFormProperty
     */
    protected void showFormProperties (TableElement table)
    {
        SelectionList categorySelList = this.performRetrieveSelectionListData (
            this.getTypeCache ().getTVersionId (StoreTypeConstants.TC_PropertyCategory), false);

        // loop through all properties of this object and display them:
        this.showFormProperty (table
                            , BOArguments.ARG_NAME
                            , MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_NAME, env)
                            , Datatypes.DT_NAME
                            , this.name);
        // display the object in the news
        this.showFormProperty (table, BOArguments.ARG_INNEWS,
            MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_INNEWS, env), Datatypes.DT_BOOL, "" + this.showInNews);
        // show object specific attributes
        // (index of oid: 5)
        this
            .showFormProperty (table, StoreArguments.ARG_CODECATEGORIES,
                MultilingualTextProvider.getText (StoreTokens.TOK_BUNDLE,
                    StoreTokens.ML_CODECATEGORIES, env), Datatypes.DT_MULTISELECT,
                this.categoriesOids, categorySelList.ids,
                categorySelList.values, 5);
    } // showFormProperties


    /***************************************************************************
     * Gets the parameters which are relevant for this object. <BR/> The <A
     * HREF="ibs.bo.BusinessObject.html#env">env</A> property is used for
     * getting the parameters. This property must be set before calling this
     * method. <BR/>
     */
    public void getParameters ()
    {
        String[] str = null;

        // get parameters relevant for super class:
        super.getParameters ();
        if ((str = this.env
            .getMultipleFormParam (StoreArguments.ARG_CODECATEGORIES)) != null)
        {
            this.categoriesOids = str;
        } // if
    } // getParameters


    /**************************************************************************
     * Sets the buttons that can be displayed when the user is in a
     * object info view. <BR/>
     * This method can be overwritten in subclasses to redefine the set of
     * buttons that can be displayed. <BR/>
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
            Buttons.BTN_SEARCH,
//            Buttons.BTN_HELP,
//            Buttons.BTN_LOGIN,
//            Buttons.BTN_SHOPPINGCART,
        }; // buttons

        // return button array
        return buttons;
    } // setInfoButtons

    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////


    /**************************************************************************
     * Change all type specific data that is not changed by performChangeData.
     * <BR/>
     * This method must be overwritten by all subclasses that have to change
     * type specific data.
     *
     * @param   action  The database connection object.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performChangeSpecificData (SQLAction action) throws DBError
    {
        int i;
        String updateStr = "";

        //-----------------------------------------------------------------
        // first delete all entries
        // room for some success statements

        // if the product profile was just created, there are no categories
        // to delete -> skip the delete categories step
        if (this.state != States.ST_CREATED)
        {
            updateStr =
                " DELETE FROM  m2_ProfileCategory_01" +
                " WHERE productProfileOid = " + this.oid.toStringQu ();
//debug (updateStr);

            // execute the queryString, indicate that we're not performing an
            // action query:
            try
            {
                action.execute (updateStr, true);
            } // try
            catch (DBError dbErr)
            {
                this.env.write (updateStr);
                // an error occurred - show name and info
                IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
            } // catch
        } // if (this.state != States.ST_CREATED)

        //-----------------------------------------------------------------
        // then insert the new tuples
        // room for some success statements
        if ((this.categoriesOids != null) && (this.categoriesOids.length > 0))
        {
            StringBuffer sb = new StringBuffer (1024);
            for (i = 0; i < this.categoriesOids.length; i++)
            {
                try
                {
                    sb.append (new OID (this.categoriesOids[i]).toStringQu ());
                } // try
                catch (IncorrectOidException e)
                {
                    // should not happen
                    throw new DBError (e);
                } // catch

                if (i != this.categoriesOids.length - 1)
                {
                    sb.append (",");
                } // if
            } // for

            updateStr =
                " INSERT INTO m2_ProfileCategory_01 (productProfileOid, categoryOid)" +
                " SELECT " + this.oid.toStringQu () + ",oid " +
                " FROM ibs_Object " +
                " WHERE oid IN (" + sb + ")";
//debug (updateStr);
            // execute the queryString, indicate that we're not performing an
            // action query:
            try
            {
                action.execute (updateStr, true);
            } // try
            catch (DBError dbErr)
            {
                this.env.write (updateStr);
                // an error occurred - show name and info
                IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
            } // catch
        } // if
    } // performChangeSpecificData


    /**************************************************************************
     * Retrieve the type specific data that is not got from the stored
     * procedure. <BR/>
     * This method must be overwritten by all subclasses that have to get type
     * specific data that cannot be got from the retrieve data stored procedure.
     *
     * @param   action  SQLAction for database operation.
     *
     * @exception    DBError
     *               This exception is always thrown, if there happens an error
     *               during accessing data.
     */
    protected void performRetrieveSpecificData (SQLAction action) throws DBError
    {
        Vector<String> ids = new Vector<String> (10, 10); // initialize elements vector
        Vector<String> names = new Vector<String> (10, 10); // initialize elements vector

        // create the SQL String to select the content of a entry
        String queryStr =
            "SELECT categoryOid, name" +
            " FROM m2_ProfileCategory_01, ibs_Object" +
            " WHERE categoryOid = oid " +
            " AND productProfileOid = " + this.oid.toStringQu ();


        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
            int rowCount = action.execute (queryStr, false);
            // no valid tuples
            if (rowCount == 0 || rowCount < 0)
            {
                return;
            } // if

            while (!action.getEOF ())
            {
                ids.addElement (action.getString ("categoryOid"));
                names.addElement (action.getString ("name"));

                // step one tuple ahead for the next loop
                action.next ();
            } // while
            // copy values of the vector to an array
            this.categoriesOids = new String[ids.size ()];
            this.categories = new String[names.size ()];
            ids.copyInto (this.categoriesOids);
            names.copyInto (this.categories);
            // the last tuple has been processed
            // end transaction
            action.end ();
        } // try
        catch (DBError dbErr)
        {
            this.env.write (queryStr);
            // an error occurred - show name and info
            IOHelpers.showMessage (dbErr, this.app, this.sess, this.env, false);
        } // catch
    } // performRetrieveSpecificData

} // class ProductProfile_01

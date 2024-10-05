/*
 * Class: LanguageStringContainer_01.java
 */

// package:
package ibs.obj.lang;

// imports:
import ibs.bo.Container;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.IOHelpers;
import ibs.service.user.User;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.NoAccessException;


/******************************************************************************
 * This class represents one object of type LanguageStringContainer with
 * version 01. <BR/>
 *
 * @version     $Id: LanguageStringContainer_01.java,v 1.52 2010/04/27 15:58:51 rburgermann Exp $
 *
 * @author      Keim Christine (CK), 981214
 ******************************************************************************
 */
public class LanguageStringContainer_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: LanguageStringContainer_01.java,v 1.52 2010/04/27 15:58:51 rburgermann Exp $";


    // HelpVariables
    /**
     * Kind of language element: message. <BR/>
     */
    public static final int MESSAGES = 0;
    /**
     * Kind of language element: exception. <BR/>
     */
    public static final int EXCEPTIONS = 1;
    /**
     * Kind of language element: token. <BR/>
     */
    public static final int TOKENS = 2;
    /**
     * Kind of language element: type. <BR/>
     */
    public static final int TYPES = 3;

    /**
     * Tables with language elements.
     */
    public static final String[] tables =
    {
        "ibs_TypeName_01",
    };


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////
    /**************************************************************************
     * This constructor creates a new instance of the class
     * LanguageStringContainer_01. <BR/>
     */
    public LanguageStringContainer_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        // initialize properties common to all subclasses:
    } // LanguageStringContainer_01


    /**************************************************************************
     * Creates a LanguageStringContainer_01 object. <BR/>
     * This constructor calls the corresponding constructor of the super class.
     *
     * @param   oid         Value for the compound object id.
     * @param   user        Object representing the user.
     *
     * @see     ibs.bo.BusinessObject
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public LanguageStringContainer_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // LanguageStringContainer_01


    /**************************************************************************
     * This method makes the class specific initializations. <BR/>
     */
    public void initClassSpecifics ()
    {
        // set common attributes:

        // set the instance's attributes:
    } // initClassSpecifics


    ///////////////////////////////////////////////////////////////////////////
    // database functions
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering:
     *                      BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      <CODE>null</CODE> => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow)
        throws NoAccessException
    {
        SQLAction action = null;        // the action object used to access the
                                        // database
        int rowCount;                   // row counter
        this.size = 0;

        // get the elements out of the database:
        // create the SQL String to select all tuples
        String queryStr = this.createQueryRetrieveContentData ();

        // execute the queryString, indicate that we're not performing an
        // action query:
        try
        {
// open db connection -  only workaround - db connection must
// be handled somewhere else
            action = this.getDBConnection ();
//trace ("exec. query: " + queryStr);
            rowCount = action.execute (queryStr, false);
//trace ("query ready.");
            // empty resultset or error?
            if (rowCount <= 0)
            {
                return;                 // terminate this method
            } // if

            // everything ok - go on
            // get tuples out of db
            while (!action.getEOF ())   // there are tuples left?
            {
//trace ("while...");
                // get specific data of container element:
                this.getContainerElementData (action, null);

//trace ("before next...");
                // step one tuple ahead for the next loop:
                action.next ();
//trace ("after next.");
            } // while

            // the last tuple has been processed
            // end transaction:
            action.end ();
        } // try
        catch (DBError e)
        {
            // an error occurred - show name and info
            IOHelpers.showMessage (e, this.app, this.sess, this.env, false);
//System.out.println ("Exception occured: " + Helpers.getStackTraceFromThrowable (e));
            throw new NoAccessException (e);
        } // catch
        finally
        {
            // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
            String temp = null;

            // preload the classes with the several language dependent strings:
            temp = ibs.util.UtilExceptions.VERSIONINFO;
            temp = ibs.bo.BOTokens.VERSIONINFO;
            temp = ibs.bo.Operations.VERSIONINFO;
            temp = ibs.di.DITokens.VERSIONINFO;
            temp = ibs.di.edi.EDITokens.VERSIONINFO;
            temp = ibs.obj.user.UserTokens.VERSIONINFO;
            temp = ibs.app.AppMessages.VERSIONINFO;
            temp = ibs.di.DIMessages.VERSIONINFO;
            temp = ibs.bo.type.TypeConstants.VERSIONINFO;
            temp = ibs.obj.query.QueryTokens.VERSIONINFO;
            temp = ibs.obj.search.SearchTokens.VERSIONINFO;
            temp = ibs.service.email.EMailMessages.VERSIONINFO;
            temp = ibs.di.service.ServiceMessages.VERSIONINFO;
            temp = ibs.service.workflow.WorkflowMessages.VERSIONINFO;
            temp = ibs.obj.workflow.WorkflowTokens.VERSIONINFO;
            temp = ibs.bo.NotifyMessages.VERSIONINFO;
// HACK:
/*
            temp = m2.diary.DiaryTokens.TOK_TERM_START_DATE;
            temp = m2.mad.MadTokens.TOK_COMPOWNER;
            temp = m2.news.NewsTokens.TOK_NEW;
            temp = m2.store.StoreTokens.TOK_PRODUCTNAME;
            temp = m2.m2Messages.VERSIONINFO;
            temp = m2.news.NewsMessages.MSG_DISCUSSIONEMPTY;
            temp = m2.store.StoreMessages.NOCATALOG;
            temp = m2.m2Types.VERSIONINFO;
*/
// ...HACK
            temp = temp.substring (0, 1);
        } // finally
    } // performRetrieveContentData


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
     * The property <B>oid</B> is already gotten. This must not be done within
     * this method. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @param   action      The action for the database connection.
     * @param   commonObj   Object representing the list element.
     *
     * @throws  DBError
     *          Error when executing database statement.
     */
    protected void getContainerElementData (SQLAction action,
                                            ContainerElement commonObj)
        throws DBError
    {
        String className;               // name of the class
        String varName;                 // name of the variable
        String varValue;                // the variable value

//trace ("in method.");
        className = action.getString ("className");
//trace ("after className.");
        varName = action.getString ("name");
//trace ("after varName.");
        varValue = action.getString ("value");
//trace ("after varValue.");


        if (varValue != null)
        {
            varValue = varValue.replace ('~', ' ');
        } // if
        else
        {
            varValue = "";
        } // else

//trace ("" + className + "." + varName + " = " + varValue);
        try
        {
            Class.forName (className).getField (varName).set (null, varValue);
        } // try
        catch (ClassNotFoundException e)
        {
            IOHelpers.showMessage (e.toString () + ": " + className,
                this.app, this.sess, this.env);
// KR just warning:
//            throw new DBError ("Error when getting the element data", e);
        } // catch
        catch (NoSuchFieldException e)
        {
            IOHelpers.showMessage (
                e.toString () + ": " + className + "." + varName,
                this.app, this.sess, this.env);
// KR just warning:
//            throw new DBError ("Error when getting the element data", e);
        } // catch
        catch (IllegalAccessException e)
        {
            IOHelpers.showMessage (
                e.toString () + ": " + className + "." + varName,
                this.app, this.sess, this.env);
            throw new DBError ("Error when getting the element data", e);
        } // catch
        catch (IllegalArgumentException e)
        {
            IOHelpers.showMessage (
                e.toString () + ": " + className + "." + varName + " \n" +
                "Probably the class or variable name is wrong (check for spaces!).",
                this.app, this.sess, this.env);
            throw new DBError ("Error when getting the element data.Probably the class or variable name is wrong (check for spaces!)", e);
        } // catch
        catch (Exception e)
        {
            IOHelpers.showMessage (
                e.toString () + ": " + className + "." + varName,
                this.app, this.sess, this.env);
            throw new DBError ("Error when getting the element data", e);
        } // catch
//trace ("set value.");
    } // getContainerElementData


    /**************************************************************************
     * Create the query to get the Strings necessary for a Language out of
     * the DB. <BR/>
     * This method can be overwritten in subclasses. <BR/>
     *
     * @return  The constructed query.
     */
    protected String createQueryRetrieveContentData ()
    {
        return
            " SELECT name, value, className" +
            " FROM " + this.tableName;
    } // createQueryRetrieveContentData


   /***************************************************************************
    * Retrieves the Strings for the Application and sets them. <BR/>
    *
    * @param   operation    Operation to be performed with the objects.
    * @param   orderBy      Property, by which the result shall be
    *                       sorted. If this parameter is null the
    *                       default order is by name.
    * @param   orderHow     Kind of ordering:
    *                       BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
    *                       <CODE>null</CODE> => BOConstants.ORDER_ASC
    *
    * @exception   NoAccessException
    *              The user does not have access to this object to perform the
    *              required operation.
    */
    public void retrieveContent (int operation, int orderBy, String orderHow)
        throws NoAccessException
    {
        this.performRetrieveContentData (operation, orderBy, orderHow);
    } // retrieveContent

} // class LanguageStringContainer_01

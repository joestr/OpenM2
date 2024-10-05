/*
 * Class: LogContainer_01.java
 */

// package:
package ibs.obj.log;

// imports:
import ibs.bo.BOConstants;
import ibs.bo.BOListConstants;
import ibs.bo.BOMessages;
import ibs.bo.BOTokens;
import ibs.bo.Buttons;
import ibs.bo.Container;
import ibs.bo.Operations;
import ibs.bo.States;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.log.LogViewElement_01;
import ibs.tech.sql.DBError;
import ibs.tech.sql.SQLAction;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;


/******************************************************************************
 * This class is a view on a BusinessObject which shows the Objects log. <BR/>
 * (Its a copy of LogContainer_01 - with very old code from HJ).
 *
 * @version     $Id: LogView_01.java,v 1.11 2010/04/13 15:55:58 rburgermann Exp $
 *
 * @author      Andreas Jansa (AJ), 011219
 ******************************************************************************
 */
public class LogView_01 extends Container
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO = "$Id: LogView_01.java,v 1.11 2010/04/13 15:55:58 rburgermann Exp $";


    /**************************************************************************
     * This constructor creates a new instance of the class NewsContainer_01.
     * <BR/>
     */
    public LogView_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();

        this.elementClassName = "ibs.obj.log.LogViewElement_01";
    } // LogView_01


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
        return new int []
        {
            Buttons.BTN_SEARCH,
        }; // buttons
    } // setContentButtons


    /**************************************************************************
     * Represent the properties of a LogContainer_01 object to the user
     * within a form. <BR/>
     *
     * @param   action  The database connection object.
     *
     * @return  The message.
     */
    protected String logMessage (int action)
    {
        String logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                BOMessages.ML_LOG_DEFAULTMESSAGE, env);

        switch (action)                 // handle direction of parameter
        {
            case Operations.OP_NEW:   // create a new object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_NEWMESSAGE, env);
                break;
            case Operations.OP_READ:  // Read the data of an object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_READMESSAGE, env);
                break;
            case Operations.OP_VIEW:  // View the object within a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWMESSAGE, env);
                break;
            case Operations.OP_EDIT: // Edit an object's properties
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_EDITMESSAGE, env);
                break;
            case Operations.OP_DELETE: // delete an objekt
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DELETEMESSAGE, env);
                break;
            case Operations.OP_LOGIN: // make a login
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_LOGINMESSAGE, env);
                break;
            case Operations.OP_VIEWRIGHTS: // view the rights
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWRIGHTSMESSAGE, env);
                break;
            case Operations.OP_EDITRIGHTS: // edit the rights of an Objekt
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_EDITRIGHTSMESSAGE, env);
                break;
 //               case Operations.OP_SETRIGHTS: // set the rights Operations.OP_EDITRIGHTS
 //                  break;
            case Operations.OP_CREATELINK: // create an Link
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_CREATELINKMESSAGE, env);
                break;
            case Operations.OP_DISTRIBUTE: // distribute an object
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DISTRIBUTEMESSAGE, env);
                break;
            case Operations.OP_ADDELEM : // add an object to an Container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_ADDELEMMESSAGE, env);
                break;
            case Operations.OP_DELELEM: // Delete an element from a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DELELEMMESSAGE, env);
                break;
            case Operations.OP_VIEWELEMS: // View the elements of a container
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_VIEWELEMSMESSAGE, env);
                break;
            default:                // unknown direction
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_DEFAULTMESSAGE, env);
                break;
        } // switch (action)

        if ((action & Operations.OP_CHANGEPROCSTATE) == Operations.OP_CHANGEPROCSTATE)
        {
            int processState = action - Operations.OP_CHANGEPROCSTATE;
            // state of order
            String stateString;
            int index = StringHelpers.findString (
                States.PST_STATEIDS, Integer.toString (processState));
            if (index != -1)
            {
                stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        States.PST_STATENAMES [index], env);
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_CHANGEPROCESSSTATE, env) + stateString;
            } // if
        } // if

        // display that a forward was performed and the according workflow state
        if ((action & Operations.OP_FORWARD) == Operations.OP_FORWARD)
        {
            int processState = action - Operations.OP_FORWARD;
            // state of order
            String stateString;
            int index = StringHelpers.findString (
                States.PST_STATEIDS, Integer.toString (processState));
            if (index != -1)
            {
                stateString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
                        States.PST_STATENAMES [index], env);
                logMessage = MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE, 
                        BOMessages.ML_LOG_FORWARDMESSAGE, env) + stateString;
            } // if ( index != -1)
        } // if ((action & Operations.OP_FORWARD) == Operations.OP_FORWARD)

        return logMessage;
    } // LogMessage


    /**************************************************************************
     * Get the Container's content out of the database. <BR/>
     * <B>THIS METHOD IS A DUMMY WHICH MUST BE OVERWRITTEN IN SUB CLASSES!</B>
     * <BR/>
     * First this method tries to load the objects from the database. During
     * this operation a rights check is done, too. If this is all right the
     * objects are returned otherwise an exception is raised. <BR/>
     *
     * @param   operation   Operation to be performed with the objects.
     * @param   orderBy     Property, by which the result shall be
     *                      sorted. If this parameter is null the
     *                      default order is by name.
     * @param   orderHow    Kind of ordering: BOConstants.ORDER_ASC or BOConstants.ORDER_DESC
     *                      null => BOConstants.ORDER_ASC
     *
     * @exception   NoAccessException
     *              The user does not have access to these objects to perform
     *              the required operation.
     */
    protected void performRetrieveContentData (int operation, int orderBy,
                                               String orderHow)
        throws NoAccessException
    {
        String orderHowLocal = orderHow; // variable for local assignments
        SQLAction action = null;        //SQLAction for Databaseoperation
        LogViewElement_01 obj;
        int rowCount = 1;

// showMessage("Name des O_" + name + "Kind of Container "+ containerKind);
//showMessage("Orderby in performRetrieveContentData-->" + orderBy);

        // ensure a correct ordering:
        if (!orderHowLocal.equalsIgnoreCase (BOConstants.ORDER_DESC)) // not descending?
        {
            orderHowLocal = BOConstants.ORDER_ASC;       // order ascending
        } // if

        this.elements.removeAllElements ();

        // get the elements out of the database:
        // create the SQL String to select all tuples
        String queryStr =
            " SELECT fullName, objectName, action, " +
            "        actionDate, pr.icon as icon " +
            " FROM   ibs_Protocol_01 pr" +
            " WHERE  (( pr.oid = " + this.oid.toStringQu () +  ")" +
            "     OR ( pr.containerId = " + this.oid.toStringQu () + ")) " +
            " ORDER BY actionDate";

        this.debug ("Query:" + queryStr);

        try
        {
            // open db connection -  only workaround - db connection must
            // be handled somewhere else
            action = this.getDBConnection ();
            rowCount = action.execute (queryStr, false);
            // empty resultset?
            if (rowCount == 0)
            {
                return;                 // terminate this method
            } // if
            // error while executing?
            else if (rowCount < 0)
            {
                return;
            } // else

            // everything ok - go on
//  showMessage("perform 2.1");
            // get tuples out of db
            while (!action.getEOF ())
            {
                // create a new object:
                //  obj = new OverlapContainerElement_01 ();
                obj = new LogViewElement_01 ();
//  showMessage("perform 3");
                obj.fullName = action.getString ("fullName");
                obj.objectName = action.getString ("objectName");
                obj.actionString = this.logMessage (action.getInt ("action"));
                obj.actionDate = action.getDate ("actionDate");
                obj.icon = action.getString ("icon");
                if ((this.sess != null) && (this.sess.activeLayout != null))
                {
                    obj.layoutpath = this.sess.activeLayout.path;
                } // if
                // add element to list of elements:
                this.elements.addElement (obj);   // add element to list of elements

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
       // close db connection in every case -  only workaround - db connection must
            // be handled somewhere else
            this.releaseDBConnection (action);
        } // finally
    } // performRetrieveContentData


    /**************************************************************************
     * Set the headings and the orderings for this container. <BR/>
     * Overload this function if you need other headings as the standard
     * container.
     */
    protected void setHeadingsAndOrderings ()
    {
        // set super attribute
        this.headings = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, 
            BOListConstants.LST_HEADINGS_LOGCONTAINER, env);

        // set super attribute
        this.orderings = new String [] {null, null, null, null};
    } // setHeadingsAndOrderings

} // class LogView_01

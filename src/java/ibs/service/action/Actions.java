/*
 * Class: Actions
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
//KR TODO: unsauber
import ibs.bo.BusinessObject;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.ml.MultilingualTextProvider;
import ibs.service.action.Action;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.ActionHandler;
import ibs.service.action.ActionMessages;
import ibs.service.action.ActionParser;
import ibs.service.action.Variables;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * Object represents the ACTIONS tag of a workflow or form definition. <BR/>
 * It holds a list of variables and actions.
 *
 * @version     $Id: Actions.java,v 1.11 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 18.10.2000
 ******************************************************************************
 */
public class Actions extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: Actions.java,v 1.11 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The variables. <BR/>
     */
    protected Variables p_vars = new Variables ();

    /**
     * The action list. <BR/>
     */
    protected Vector<Action> p_actions = new Vector<Action> ();


    /**************************************************************************
     * Constructor. <BR/>
     */
    public Actions ()
    {
        // nothing to do
    } // Actions


    /**************************************************************************
     * Initializes the object with all variables and actions defined in the
     * given ACTIONS node. <BR/>
     *
     * @param   actionsNode     the 'ACTIONS' node of the DOM tree
     * @param   env             The current environment
     *
     * @throws  ActionException
     *          There occurred a problem.
     */
    public void loadActions (Node actionsNode, Environment env) throws ActionException
    {
        // a ACTIONS element can contain an optional VARIABLES element
        // and one or more ACTION elements.

        // check if there are more nodes
        if (actionsNode != null && actionsNode.hasChildNodes ())
        {
            ActionParser parser = new ActionParser ();

            boolean varsFound = false;
            // get the child nodes
            NodeList nodelist = actionsNode.getChildNodes ();
            int size = nodelist.getLength ();
            // iterate through child-nodes
            for (int i = 0; i < size; i++)
            {
                // get next child-node
                Node node = nodelist.item (i);
                // select only element node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // if the node is the VARIABLES node
                    if (node.getNodeName ().equals (ActionConstants.ELEM_VARIABLES))
                    {
                        // if there are more than one VARIABLES nodes
                        // report this as error.
                        if (varsFound)
                        {
                            throw new ActionException (
                                MultilingualTextProvider.getMessage (ActionMessages.MSG_BUNDLE,
                                    ActionMessages.ML_MSG_DUPLICATE_VARIABLES, env));
                        } // if (p_vars != null)
                        varsFound = true;
                        // extract the variables from the node
                        this.p_vars = parser.extractVariables (node, env);
                    } // if the node is the VARIABLES node
                    // if the node is the VARIABLES node
                    else if (node.getNodeName ().equals (ActionConstants.ELEM_ACTION))
                    {
                        // extract the action and add it to the vector.
                        this.p_actions.addElement (parser.extractAction (node));
                    } // if the node is a ACTION node
                    else
                    {
//                        throw new ActionException (ActionMessages.MSG_INVALID_NODE);
                    } // else if unknown node
                } // if (node.getNodeType() == Node.ELEMENT_NODE)
            } // for i
        } // if valid actions node
    } // loadActions


    /**************************************************************************
     * Initializes the object with all variables and actions defined in the
     * given ACTIONS node. <BR/>
     *
     * @param   obj     The object to be initialized.
     * @param   env     The actual environment.
     *
     * @return  the resulting variables
     *
     * @throws  ActionException
     *          There occurred a problem.
     */
    public Variables performActions (BusinessObject obj, Environment env)
        throws ActionException
    {
        // ATTENTION!!
        // Starting with version 2.3 the document templates are cached
        // in the ApplicationInfo object. This means that all objects in
        // the dataElement of the document template are shared between all
        // sessions. So we have to ensure that the properties of the current
        // Actions object are NOT changed.

        // get a copy if the variables
        Variables vars = this.p_vars.getCopy ();

        // get a copy if the actions vector
        Vector<Action> actions = new Vector<Action> ();
        for (int i = 0; i < this.p_actions.size (); i++)
        {
            Action action = this.p_actions.elementAt (i);
            actions.addElement (action.getCopy ());
        } // for i

        // initialize the system variables (OID, USERID, USERNAME, DATE, ...)
        // with the varies because of the given BusinessObject.
        vars.addSysVars (obj);

        ActionHandler handler = new ActionHandler ();
        handler.initObject (OID.getEmptyOid (), obj.user, env, obj.sess, obj.app);

        // perform all actions
        int noActions = actions.size ();
        for (int i = 0; i < noActions; i++)
        {
            Action action = actions.elementAt (i);
            handler.performAction (obj, action, vars);
        } // for i

        // return the resulting variables
        return vars;
    } // performActions

} // class Actions

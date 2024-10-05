/*
 * Class: ActionParser.java
 */

// package:
package ibs.service.action;

// imports:
import ibs.BaseObject;
import ibs.io.Environment;
import ibs.service.action.Action;
import ibs.service.action.ActionConstants;
import ibs.service.action.ActionException;
import ibs.service.action.ActionParameter;
import ibs.service.action.Variable;
import ibs.service.action.Variables;

import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;


/******************************************************************************
 * Extracts data from xml-action-definitions. <BR/>
 *
 * @version     $Id: ActionParser.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class ActionParser extends BaseObject
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: ActionParser.java,v 1.9 2010/04/07 13:37:12 rburgermann Exp $";


    /**************************************************************************
     * traverses a VARIABLES section. <BR/>
     *
     * @param   variablesNode   root node of the subtree to traverse
     * @param   env             The current environment
     *
     * @return  Variables object which holds all variables specified in wf-definition
     *
     * @throws  ActionException
     *          An error occurred.
     */
    public Variables extractVariables (Node variablesNode, Environment env)
        throws ActionException
    {
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        Variable aVariable;

        // initialize a new Variables list
        Variables aVariablesList = new Variables ();

        //
        // get subnodes of VARIABLES
        // - VARIABLE
        //

        // check if there are more nodes
        if (variablesNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = variablesNode.getChildNodes ();
            size = nodelist.getLength ();

            // iterate through child-nodes
            for (int i = 0; i < size; i++)
            {
                // get next child-node
                node = nodelist.item (i);

                // test type of node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // normalize the node to ensure that all
                    // text values are together
                    node.normalize ();
                    // get name of node
                    nodename = node.getNodeName ();

                    // get the VARIABLE element
                    if (nodename.equals (ActionConstants.ELEM_VARIABLE))
                    {
                        // create variable representation
                        aVariable = this.traverseVariable (node);
                        // add new variable to variable list
                        aVariablesList.addEntry (aVariable, env);
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes())

        // exit method
        return aVariablesList;
    } // traverseVariables


    /**************************************************************************
     * traverses VARIABLE section of VARIABLES-block. <BR/>
     *
     * @param variableNode  root node of the subtree to traverse
     *
     * @return Variable object wich represents one xml-variable
     */
    private Variable traverseVariable (Node variableNode)
    {
        // local variables
        String varName = null;
        String varType = null;
        String varLen = null;
        String varDesc = null;
        String varValue = null;

        //
        // get subnodes of VARIABLE
        // - NAME
        // - TYPE
        // - LENGTH
        // - DESCRIPTION
        // - VALUE
        //

        // check if there are more nodes
        if (variableNode.hasChildNodes ())
        {
            // get the child nodes
            NodeList nodelist = variableNode.getChildNodes ();
            int size = nodelist.getLength ();

            // iterate through child-nodes
            for (int i = 0; i < size; i++)
            {
                // get next child-node
                Node node = nodelist.item (i);
                // test type of node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // normalize the node to ensure that all
                    // text values are together
                    node.normalize ();
                    // get name of node
                    String nodename = node.getNodeName ();

                    // get the VARIABLENAME element
                    if (nodename.equals (ActionConstants.ELEM_VARIABLENAME))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        Text text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // add the post and prefix to the variable name
                            varName = ActionConstants.VARIABLE_PREFIX +
                                      text.getNodeValue () +
                                      ActionConstants.VARIABLE_POSTFIX;
                        } // if (text != null)
                    } // if (nodename ... ELEM_VARIABLENAME
                    // get the VARIABLEVALUE element
                    else if (nodename.equals (ActionConstants.ELEM_VARIABLEVALUE))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        Text text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            varValue = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLEVALUE
                    // get the TYPE element
                    else if (nodename.equals (ActionConstants.ELEM_VARIABLETYPE))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        Text text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            varType = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLETYPE
                    // get the LENGTH element
                    else if (nodename.equals (ActionConstants.ELEM_VARIABLELENGTH))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        Text text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            varLen = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLELENGTH
                    // get the DESCRIPTION element
                    else if (nodename.equals (ActionConstants.ELEM_VARIABLEDESCRIPTION))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        Text text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            varDesc = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLEDESCRIPTION
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (notifyNode.hasChildNodes ())

        // return the new variable object
        return new Variable (varName, varType, varLen, varDesc, varValue);
    } // traverseVariable


    /**************************************************************************
     * traverses a ACTION section. <BR/>
     *
     * @param   actionNode    node of the subtree to traverse
     *
     * @return  The action which is extracted from the ACTION section.
     */
    public Action extractAction (Node actionNode)
    {
        NamedNodeMap attributes;
        Node node;
        NodeList nodelist;
        int size;
        String nodename;

        // initialize a new action object
        Action action = new Action ();

        //
        // ACTION header information
        // - TYPE
        //
        // get attributes of node
        attributes = actionNode.getAttributes ();
        // get the actions type from the TYPE attribute
        node = attributes.getNamedItem (ActionConstants.ATTR_ACTIONTYPE);
        if (node != null)
        {
            action.type = node.getNodeValue ();
        } // if
        else
        {
            action.type = ActionConstants.UNDEFINED;
        } // else

        //
        // get subnodes of ACTION
        // - CALL
        // - INPARAMS
        // - OUTPARAMS
        //

        // check if there are more nodes
        if (actionNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = actionNode.getChildNodes ();
            size = nodelist.getLength ();

            // iterate through child-nodes
            for (int i = 0; i < size; i++)
            {
                // get next child-node
                node = nodelist.item (i);

                // test type of node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // normalize the node to ensure that all
                    // text values are together
                    node.normalize ();
                    // get name of node
                    nodename = node.getNodeName ();

                    // get the CALL element
                    if (nodename.equals (ActionConstants.ELEM_CALL))
                    {
                        // traverse INPARAMS section and extract value
                        action.call = this.traverseCall (node);
                    } // if (nodename ...
                    // get the INPARAMS element
                    else if (nodename.equals (ActionConstants.ELEM_INPARAMS))
                    {
                        // traverse INPARAMS section and extract values
                        action.inParams = this.traverseParams (node);
                    } // if (nodename ...
                    // get the INPARAMS element
                    else if (nodename.equals (ActionConstants.ELEM_OUTPARAMS))
                    {
                        // traverse OUTPARAMS section and extract values
                        action.outParams = this.traverseParams (node);
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes())

        // return the action object
        return action;
    } // extractAction


    /**************************************************************************
     * traverses CALL section of one ACTION. <BR/>
     *
     * @param callNode  root node of the subtree to traverse
     *
     * @return  String  the value of the CALL tag
     */
    private String traverseCall (Node callNode)
    {
        // local variables
        Text text;
        String callString;

        // normalize the node to ensure that all text values are together
        callNode.normalize ();

        // get text value
        text = (Text) callNode.getFirstChild ();
        if (text != null)
        {
            callString = text.getNodeValue ();
        } // if
        else
        {
            callString = ActionConstants.UNDEFINED;
        } // else

        // exit
        return callString;
    } // traverseCall


    /**************************************************************************
     * traverses a IN/OUTPARAMS section in ACTION. <BR/>
     *
     * @param paramsNode    node of the subtree to traverse
     *
     * @return  vector of Parameter-objects extracted from given node
     */
    private Vector<ActionParameter> traverseParams (Node paramsNode)
    {
        // local variables
        Node node;
        NodeList nodelist;
        int size;
        String nodename;

        // initialize new parameters list
        Vector<ActionParameter> params = new Vector<ActionParameter> ();

        //
        // no IN/OUTPARAMS attributes
        //

        //
        // get sub nodes of IN/OUTPARAMS
        // - PARAMETER
        //

        // check if there are more nodes
        if (paramsNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = paramsNode.getChildNodes ();
            size = nodelist.getLength ();

            // iterate through child-nodes
            for (int i = 0; i < size; i++)
            {
                // get next child-node
                node = nodelist.item (i);

                // test type of node
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // normalize the node to ensure that all
                    // text values are together
                    node.normalize ();
                    // get name of node
                    nodename = node.getNodeName ();

                    // get the PARAMETER elements
                    if (nodename.equals (ActionConstants.ELEM_PARAMETER))
                    {
                        // traverse PARAMETER section
                        ActionParameter param = this.traverseParameter (node);
                        // add parameter to list
                        params.addElement (param);
                    } // if
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes())

        // exit method
        return params;
    } // traverseParams


    /**************************************************************************
     * traverses PARAMETER section of IN/OUTPARAMS. <BR/>
     *
     * @param   parameterNode   Root node of the subtree to traverse.
     *
     * @return  An ActionParameter object.
     */
    private ActionParameter traverseParameter (Node parameterNode)
    {
        // local variables
        String parameterValue = ActionConstants.UNDEFINED;
        String fieldName = null;

        // normalize the node to ensure that all text values are together
        parameterNode.normalize ();

        // get text value
        Text text = (Text) parameterNode.getFirstChild ();
        if (text != null)
        {
            parameterValue = text.getNodeValue ();
        } // if

        // get attributes of the parameter node
        NamedNodeMap attributes = parameterNode.getAttributes ();
        // get the optional NAME attribute node
        Node node = attributes.getNamedItem (ActionConstants.ATTR_FIELDNAME);
        if (node != null)
        {
            fieldName = node.getNodeValue ();
        } // if (node != null)

        return new ActionParameter (parameterValue, fieldName);
    } // traverseParameter

} // ActionParser

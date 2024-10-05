/*
 * Class: WorkflowParser.java
 */

// package:
package ibs.obj.workflow;

// imports:
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.DataElementList;
import ibs.di.filter.Filter;
import ibs.ml.MultilingualTextProvider;
import ibs.service.action.ActionParameter;
import ibs.service.workflow.Action;
import ibs.service.workflow.Condition;
import ibs.service.workflow.Notify;
import ibs.service.workflow.Receiver;
import ibs.service.workflow.RegisterObserverJob;
import ibs.service.workflow.State;
import ibs.service.workflow.Transition;
import ibs.service.workflow.Variable;
import ibs.service.workflow.Variables;
import ibs.service.workflow.Workflow;
import ibs.service.workflow.WorkflowConstants;
import ibs.service.workflow.WorkflowMessages;
import ibs.service.workflow.WorkflowTagConstants;
import ibs.tech.xml.XMLReader;
import ibs.tech.xml.XMLReaderException;
import ibs.util.file.FileHelpers;

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/******************************************************************************
 * Parses and extracts data from xml-workflow-definition file. <BR/>
 *
 * @version     $Id: WorkflowParser.java,v 1.20 2010/04/07 13:37:10 rburgermann Exp $
 *
 * @author      Horst Pichler (HP), 2.10.2000
 ******************************************************************************
 */
public class WorkflowParser extends Filter
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: WorkflowParser.java,v 1.20 2010/04/07 13:37:10 rburgermann Exp $";


    /**
     * Holds java representation of xml-workflow-definition. <BR/>
     */
    private Workflow workflow;

    ///////////////////////////////////////////////
    //
    // Log object - must be set from outside
    //      --> holds messages/errors/warnings created during parsing
    //

    /**
     * Holds log for workflow-actions, messages, errors and warnings. <BR/>
     */
    private WorkflowLog log;


    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Creates an WorkflowDefinition Object. <BR/>
     */
    public WorkflowParser ()
    {
        // nothing to do
    } // constructor


    ///////////////////////////////////////////////////////////////////////////
    // unused methods,
    // must be declared: declared as ABSTRACT in super-class ibs.di.Filter
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This is some kind of initialization procedure that must be called before
     * an import starts.
     * The init method reads in the import file.
     * The init method should read the file name of the
     * import script.
     *
     * @return true initialisation succeeded or false otherwise
     */
    public boolean init ()
    {
        return false;
    } // init


    /**************************************************************************
     * Tests if there are more objects available from this import file. <BR/>
     *
     * @return true if there are more objects false otherwise
     */
    public boolean hasMoreObjects ()
    {
        return false;
    } // hasMoreObjects


    /**************************************************************************
     * Returns the next DataElement from this importFile. <BR/>
     *
     * @return an DataElement Object that holds the data of an object in the
     *          importFile
     */
    public DataElement nextObject ()
    {
        return null;
    } // nextObject


    /**************************************************************************
     * Returns the next DataElementList from this importFile. <BR/>
     *
     * @return an DataElementList object that holds a collection of objects
     *          the importScript can be applied on
     */
    public DataElementList nextObjectCollection ()
    {
        return null;
    } // nextObjectCollection


    /**************************************************************************
     * Creates an XML document out of an array of dataElements.
     * This is the method that must be implemented in the subclasses. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean create (DataElement[] dataElements)
    {
        return false;
    } // create


    /**************************************************************************
     * Adds a set of object definitions to the export document.
     * In case there is no export document created already it will be
     * initialized first. <BR/>
     *
     * @param dataElements  a dataElement array to construct the export
     *                      document from
     *
     * @return  true if the export document has been created sucuessfully
     *          or false otherwise
     */
    public boolean add (DataElement[] dataElements)
    {
        return false;
    } // add


    /**************************************************************************
     * Sets processing again to the beginning of all objects. <BR/>
     */
    public void reset ()
    {
        // nothing to do
    } // reset


    /**************************************************************************
     * This method reads the workflow-definition, performs the parsing and
     * calls the methods to store all needed data in the returned
     * Workflow object. <BR/>
     *
     * @param   fileName    Filename of workflow template.
     * @param   path        Absolute path to workflow template.
     * @param   log         For error logging.
     *
     * @return  Structure that represents a workflow definition.
     *          <CODE>null</CODE> if error occurred.
     */
    public Workflow parse (String fileName, String path, WorkflowLog log)
    {
        // initialize:
        this.workflow = new Workflow (); // holds definition structure

        // set log object:
        this.log = log;

        // set filename and path for xml-definition:
        if (fileName == null || path == null)
        {
            // log entry: error - filename incorrect
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_DOMERROR_OR_FILENOTFOUND, env) +
                "[" + this.path + this.fileName + "]",
                true);

            // CRITICAL ERROR: exit
            return null;
        } // if (filename == null ...

        // set attributes:
        this.fileName = fileName;
        this.path = path;

        try
        {
            // read the xml document from a file - convert it
            // in a parseable tree:
            XMLReader reader = new XMLReader (
                FileHelpers.addEndingFileSeparator (this.path) + this.fileName,
                true, null);

            // get the documentRoot:
            Element root = reader.getRootElem ();

            // extract instance data from xml-file; start with root-node
            // check if extraction succeeded
            if (!this.parseWorkflowDefinition (root))
            {
                // log entry: error - error while parsing
                this.log.add (DIConstants.LOG_ERROR,
                    MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                        WorkflowMessages.ML_MSG_PARSE_ERROR, env),
                    true);

                // CRITICAL ERROR: exit
                return null;
            } // if
        } // try
        catch (XMLReaderException e)
        {
            // log entry: problem when parsing xml-definition
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_DOMERROR_OR_FILENOTFOUND, env) +
                "[" + this.path + this.fileName + "]",
                true);

            // CRITICAL ERROR: exit
            return null;
        } // catch

        // definition retrieved - now make some checks:
        // 1. startstate defined?
        if (this.workflow.startStateName.equalsIgnoreCase (WorkflowConstants.UNDEFINED))
        {
            // log entry: error - no startState defined
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_START_STATE_UNDEF, env),
                true);

            // CRITICAL ERROR: exit
            return null;
        } // if
        // 2. endstate (s) defined?
        else if (this.workflow.endStates == null ||
                 (this.workflow.endStates != null &&
                  this.workflow.endStates.size () == 0))
        {
            // log entry: error - no endState defined
            this.log.add (DIConstants.LOG_ERROR,
                MultilingualTextProvider.getMessage (WorkflowMessages.MSG_BUNDLE,
                    WorkflowMessages.ML_MSG_END_STATE_UNDEF, env),
                 true);
            // CRITICAL ERROR: exit
            return null;
        } // else if

//debug ("WorkflowParser.parse END workflow = " + this.workflow.toString ());
        // sucess - exit
        return this.workflow;
    } // parse


    /**************************************************************************
     * Traverses the m2Workflow file and extracts the needed information
     * like: states, startStateName, processManager, .... <BR/>
     * This information will be stored in attributes of this class.
     *
     * @param root      root node of the subtree to traverse
     *
     * @return          success flag
     */
    private boolean parseWorkflowDefinition (Element root)
    {
        // local variables
        boolean isTraverseOk = false;   // return value
        NodeList nodeList;              // list of xml-nodes
        Node node;                      // one node in the xml-definition
        NamedNodeMap attributes;        // list of attributes for 1 node
        String nodename;                // name of actual node

        // proceed only if root-node exists
        if (root != null)
        {
            // get name of root node
            nodename = root.getNodeName ();

            // proceed only if this is the WORKFLOW element
            if (nodename != null && nodename.equals (WorkflowTagConstants.ELEM_WORKFLOW))
            {
                // get all attributes of root-node
                attributes = root.getAttributes ();

                // now extract information and store it in class attributes

                // 1. retrieve workflow header information
                // - VERSION
                // - NAME
                // - DESCRIPTION
                // - LOGFILEPATH
                // - DISPLAYLOG
                // - CONFIRMOPERATION
                // - STARTSTATE
                // - PROCESSMANAGER
                // - PROCESSMANAGERDEST
                // - PROCESSMGRRIGHTS
                // - REMAINRIGHTSSTARTER
                // - REMAINRIGHTSOTHERS
                // - PATHAFTERTRANSITION

                // VERSION
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_WORKFLOWVERSION);
                // check if the VERSION attribute set
                if (node != null)
                {
                    this.workflow.version = node.getNodeValue ();
                } // if
                else
                {
                    // not found: set to undefined
                    this.workflow.version = WorkflowConstants.UNDEFINED;
                } // else

                // NAME
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_WORKFLOWNAME);
                // check if the NAME attribute set
                if (node != null)
                {
                    this.workflow.name = node.getNodeValue ();
                } // if
                else
                {
                    // not found: set to undefined
                    this.workflow.name = WorkflowConstants.UNDEFINED;
                } // else

                // DESCRIPTION
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_WORKFLOWDESCRIPTION);
                // check if the DESCRIPTION attribute set
                if (node != null)
                {
                    this.workflow.description = node.getNodeValue ();
                } // if
                else
                {
                    // not found: set to undefined
                    this.workflow.description = WorkflowConstants.UNDEFINED;
                } // else

                // WRITELOG
                this.workflow.writeLog = true;
                this.workflow.writeLogValue = WorkflowConstants.YES;
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_WRITELOG);
                // check if there is an ATTR_WRITELOG attribute set
                if (node != null)
                {
                    this.workflow.writeLogValue = node.getNodeValue ();
                    if (this.workflow.writeLogValue.equalsIgnoreCase (WorkflowConstants.NO))
                    {
                        this.workflow.writeLog = false;
                    } // if
                } // if

                // DISPLAYLOG
                this.workflow.displayLog = true;
                this.workflow.displayLogValue = WorkflowConstants.YES;
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_DISPLAYLOG);
                // check if there is an DISPLAYLOG attribute set
                if (node != null)
                {
                    this.workflow.displayLogValue = node.getNodeValue ();
                    if (this.workflow.displayLogValue.equalsIgnoreCase (WorkflowConstants.NO))
                    {
                        this.workflow.displayLog = false;
                    } // if
                } // if

                // CONFIRMOPERATION
                this.workflow.confirmOperation = true;
                this.workflow.confirmOperationValue = WorkflowConstants.YES;
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_CONFIRMOPERATION);
                // check if there is an CONFIRMOPERATION attribute set
                if (node != null)
                {
                    this.workflow.confirmOperationValue = node.getNodeValue ();
                    if (this.workflow.confirmOperationValue.equalsIgnoreCase (WorkflowConstants.NO))
                    {
                        this.workflow.confirmOperation = false;
                    } // if
                } // if

                // STARTSTATE
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_STARTSTATE);
                // check if there is an START attribute set
                if (node != null)
                {
                    this.workflow.startStateName = node.getNodeValue ();
                } // if
                else
                {
                    // not found: set to undefined
                    this.workflow.startStateName = WorkflowConstants.UNDEFINED;
                } // else

                // PROCESSMGR
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_PROCESSMGR);
                // check if there is an PROCESSMGR attribute set
                if (node != null)
                {
                    this.workflow.processMgr.name = node.getNodeValue ();
                } // if

                // PROCESSMGRDEST
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_PROCESSMGRDEST);
                // check if there is an PROCESSMGRDEST attribute set
                if (node != null)
                {
                    this.workflow.processMgr.destination = node.getNodeValue ();
                } // if

                // PROCESSMGRRIGHTS (rights for process manager)
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_PROCESSMGRRIGHTS);
                // check if there is an PROCESSMGRRIGHTS attribute set
                if (node != null)
                {
                    this.workflow.processMgr.rights = node.getNodeValue ();
                } // if
                else
                {
                    // default setting
                    this.workflow.processMgr.rights = WorkflowConstants.DEFAULT_MANAGERRIGHTS;
                } // else

                // REMAINRIGHTSSTARTER (rights for existing links)
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_REMAINRIGHTSSTARTER);
                // check if there is an REMAINRIGHTSSTARTER attribute set
                if (node != null)
                {
                    this.workflow.starter.rights = node.getNodeValue ();
                } // if
                else
                {
                    // default setting:
                    this.workflow.starter.rights = WorkflowConstants.DEFAULT_REMAINRIGHTSSTARTER;
                } // else

                // REMAINRIGHTSOTHERS (rights for existing links)
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_REMAINRIGHTSOTHERS);
                // check if there is an REMAINRIGHTSOTHERS attribute set
                if (node != null)
                {
                    this.workflow.remainRightsOthers = node.getNodeValue ();
                } // if
                else
                {
                    // default setting
                    this.workflow.remainRightsOthers =
                        WorkflowConstants.DEFAULT_REMAINRIGHTSOTHERS;
                } // else

                // IGNORERIGHTS (indicates if rights-settings shall be used or not)
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_SETRIGHTS);
                // check if there is an IGNORERIGHTS attribute set
                if (node != null)
                {
                    if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_DEFAULT))
                    {
                        this.workflow.setRights = WorkflowConstants.SETRIGHTS_DEFAULT;
                    } // if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_DEFAULT))
                    else if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_NONE))
                    {
                        this.workflow.setRights = WorkflowConstants.SETRIGHTS_NONE;
                    } // else if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_NONE))
                    else if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_INHERIT))
                    {
                        this.workflow.setRights = WorkflowConstants.SETRIGHTS_INHERIT;
                    } // else if (node.getNodeValue ().equalsIgnoreCase (WorkflowConstants.TOK_SETRIGHTS_INHERIT))
                    else    // invalid IGNORERIGHTS VALUE
                    {
                        // set the default setting
                        this.workflow.setRights = WorkflowConstants.SETRIGHTS_DEFAULT;

                    } // else invalid IGNORERIGHTS VALUE
                } // if (node != null)
                else    // IGNORERIGHTS NOT SET
                {
                    // set the default setting
                    this.workflow.setRights = WorkflowConstants.SETRIGHTS_DEFAULT;
                } // else IGNORERIGHTS NOT SET

                // PATHAFTERTRANSITION (path to go to after a transition)
                node = attributes.getNamedItem (WorkflowTagConstants.ATTR_PATHAFTERTRANSITION);
                // check if there is an IGNORERIGHTS attribute set
                if (node != null)
                {
                    this.workflow.pathAfterTransition = node.getNodeValue ();
                } // if (node != null)


                // 2. all states including their attributes
                // retrieve child nodes: VARIABLE & STATE collection
                if (root.hasChildNodes ())
                {
                    // store the objects and the total amount of objects found
                    nodeList = root.getChildNodes ();
                    this.elementsLength = this.checkElementsLength (nodeList);
                    this.getWorkflowChildNodes (nodeList);
                    // Version2.0 TAG: VARIABLES

                    // success: set return value
                    isTraverseOk = true;
                } //if (root.hasChildNodes ())
            } // else nodename is IMPORT
        } // else node exists

        // return success
        return isTraverseOk;
    } // initWorkflow


    /**************************************************************************
     * Get VARIABLE and STATE collection from xml-definition. <BR/>
     *
     * @param   nodeList    The xml nodes.
     *
     * @return  <CODE>true</CODE>, <CODE>false</CODE>.
     */
    private boolean getWorkflowChildNodes (NodeList nodeList)
    {
        // initialize local variables
        Node node;              // one node
        State aState;               // <STATE ... > representation
        Variables aVariablesList;   // <VARIABLES> representation
        int counter = 0;            // node counter

        // does state node list exist?
        if (nodeList != null)
        {
            // loop through nodes
            while (counter < nodeList.getLength ())
            {
                // get the next node
                node = nodeList.item (counter++);

                // check if node is an element
                if (node.getNodeType () == Node.ELEMENT_NODE)
                {
                    // extract workflow nodes
                    // differ between different node-types
                    // - STATE
                    // - VARIABLES
                    if (node.getNodeName ().equals (WorkflowTagConstants.ELEM_STATE))
                    {
                        // create state with elements from xml-file
                        aState = this.traverseState (node);

                        // add new state to workflows statelist
                        this.workflow.stateList.addElement (aState);
                    } // if (node.getNodeName () ...
                    else if (node.getNodeName ().equals (WorkflowTagConstants.ELEM_VARIABLES))
                    {
                        // create variables-list with elements from xml-file
                        aVariablesList = this.traverseVariables (node);

                        // add variables-list to workflow-representation
                        this.workflow.variables = aVariablesList;
                    } // else if (node.getNodeName () ...
                    else
                    {
                        //
                        // MISSING: ERROR-PROTOCOL (warning)
                        //
                    } // else node not valid therefore ignore it
                } //if (node.getNodeType () == Node.ELEMENT_NODE)
            } // while (counter < nodeList.getLength)
        } // nodeList != null
        else
        {
            // nodelist empty - exit
            //
            // MISSING: ERROR-PROTOCOL
            //
            return false;
        } // else

        // exit
        return true;
    } // getWorkflowChildNodes


    /**************************************************************************
     * traverses a VARIABLES section. <BR/>
     *
     * @param variablesNode      root node of the subtree to traverse
     *
     * @return  Variables object which holds all variables specified in wf-definition
     */
    private Variables traverseVariables (Node variablesNode)
    {
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        Variable aVariable;

        // initialize a new Variables list
        Variables aVariablesList = new Variables ();

        //
        // no VARIABLES header information
        //

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
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLE))
                    {
                        // create variable representation
                        aVariable = this.traverseVariable (node);

                        // add new variable to variable list
                        aVariablesList.addEntry (aVariable);
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

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
        Node node;
        NodeList nodelist;
        int size;
        Text text;
        String nodename;
        Variable variable = new Variable ();

        //
        // no VARIABLE header information
        //

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
            nodelist = variableNode.getChildNodes ();
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

                    // get the VARIABLENAME element
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLENAME))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            variable.name = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLENAME
                    // get the VARIABLEVALUE element
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLEVALUE))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            variable.value = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLEVALUE
                    // get the TYPE element
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLETYPE))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            variable.type = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLETYPE
                    // get the LENGTH element
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLELENGTH))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            variable.length = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLELENGTH
                    // get the DESCRIPTION element
                    if (nodename.equals (WorkflowTagConstants.ELEM_VARIABLEDESCRIPTION))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            variable.description = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_VARIABLEDESCRIPTION
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (notifyNode.hasChildNodes ())

        // exit method
        return variable;
    } // traverseVariable


    /**************************************************************************
     * traverses a STATE section. <BR/>
     *
     * @param   stateNode   Root node of the subtree to traverse.
     *
     * @return  The traversed state.
     */
    private State traverseState (Node stateNode)
    {
        NamedNodeMap attributes;
        Node node;
        NodeList nodelist;
        int size;
        String nodename;

        // initialize a new state
        State aState = new State ();

        //
        // get STATE header information
        //

        // get attributes of node
        attributes = stateNode.getAttributes ();

        //get the state's name from the NAME attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_STATENAME);
        if (node != null)
        {
            aState.name = node.getNodeValue ();
        } // if

        //get the state's type from the TYPE attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_STATETYPE);
        if (node != null)
        {
            aState.type = node.getNodeValue ();
        } // if

        //get the state's description from the DESCRIPTION attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_STATEDESCRIPTION);
        if (node != null)
        {
            aState.type = node.getNodeValue ();
        } // if

        //get the state's description from the CONFIRMOPERATION attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_CONFIRMOPERATION);
        if (node != null)
        {
            aState.confirmOperation = node.getNodeValue ();
        } // if

        //
        // get subnodes of state
        // - ACTION (v2.0)
        // - RECEIVER (v1.0), CC (v1.0)
        // - APPLICATION (v2.0)
        // - TRANSITION (v2.0), NEXTSTATE (v1.0)
        // - NOTIFICATION (v2.0), MESSAGE (v1.0)
        //

        // check if there are more nodes
        if (stateNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = stateNode.getChildNodes ();
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

                    // Version1.0 TAGS:
                    // RECEIVER
                    if (nodename.equals (WorkflowTagConstants.ELEM_RECEIVER))
                    {
                        this.traverseReceiver (node, aState);
                    } // if
                    // get the CC element  including DESTINATION
                    else if (nodename.equals (WorkflowTagConstants.ELEM_CC))
                    {
                        this.traverseCC (node, aState);
                    } // else if
                    // get the NEXTSTATE element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_NEXTSTATE))
                    {
                        this.traverseV1NextState (node, aState);
                    } // else if
                    // get the MESSAGE element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_MESSAGE))
                    {
                        this.traverseMessage (node, aState);
                    } // else if
                    // Version2.0 TAGS:
                    // get the TRANSITION element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_TRANSITION))
                    {
                        this.traverseTransition (node, aState);
                    } // else if
                    // get the ACTION element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_ACTION))
                    {
                        this.traverseAction (node, aState);
                    } // else if
                    // get the NOTIFICATION element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFICATION))
                    {
                        this.traverseNotification (node, aState);
                    } // else if

                    // Version3.0 TAGS:
                    else if (nodename.equals (WorkflowTagConstants.ELEM_REGISTEROBSERVER))
                    {
                        this.traverseRegister (node, aState);
                    } // else if
                    else if (nodename.equals (WorkflowTagConstants.ELEM_UNREGISTEROBSERVER))
                    {
                        this.traverseUnregister (node, aState);
                    } // else if
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // exit method
        return aState;
    } // traverseState


    /**************************************************************************
     * traverses a ACTION section in STATE. <BR/>
     *
     * @param actionNode    node of the subtree to traverse
     * @param aState        workflow state, represented as an object
     */
    private void traverseAction (Node actionNode, State aState)
    {
        NamedNodeMap attributes;
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        String value;
        boolean isExecuteOnStart = false;

        // initialize a new action object
        Action action = new Action ();

        //
        // ACTION header information
        // - TYPE
        //
        // get attributes of node
        attributes = actionNode.getAttributes ();
        // get the actions type from the TYPE attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_ACTIONTYPE);
        if (node != null)
        {
            action.type = node.getNodeValue ();
        } // if
        else
        {
            action.type = WorkflowConstants.UNDEFINED;
        } // else

        // get the actions type from the TYPE attribute
        node = attributes.getNamedItem (WorkflowTagConstants.ATTR_EXECUTEONSTART);
        if (node != null)
        {
            // check if AFTERTRANSITION="YES" is activated
            if ((value = node.getNodeValue ()) != null)
            {
                isExecuteOnStart = value.equals (WorkflowConstants.YES);
            } // if
        } // if (node != null)


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
                    if (nodename.equals (WorkflowTagConstants.ELEM_CALL))
                    {
                        // traverse INPARAMS section and extract value
                        action.call = this.traverseCall (node);
                    } // if (nodename ...
                    // get the INPARAMS element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_INPARAMS))
                    {
                        // traverse INPARAMS section and extract values
                        action.inParams = this.traverseParams (node);
                    } // if (nodename ...
                    // get the INPARAMS element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_OUTPARAMS))
                    {
                        // traverse OUTPARAMS section and extract values
                        action.outParams = this.traverseParams (node);
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // should the action be executed after the transition?
        if (isExecuteOnStart)
        {
            aState.executeOnStartActions.addElement (action);
        } // if
        else                            // add action to states action list
        {
            aState.actions.addElement (action);
        } // else
    } // traverseAction


    /**************************************************************************
     * traverses CALL section of one ACTION. <BR/>
     *
     * @param callNode  root node of the subtree to traverse
     *
     * @return  String  the value of the CALL tag
     */
    private String traverseCall (Node callNode)
    {
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
            callString = WorkflowConstants.UNDEFINED;
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
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        ActionParameter param;

        // initialize new parameters list
        Vector<ActionParameter> params = new Vector<ActionParameter> ();

        //
        // no IN/OUTPARAMS attributes
        //

        //
        // get subnodes of IN/OUTPARAMS
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
                    if (nodename.equals (WorkflowTagConstants.ELEM_PARAMETER))
                    {
                        // traverse PARAMETER section
                        param = this.traverseParameter (node);

                        // add parameter to list
                        params.addElement (param);
                    } // if
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // exit method
        return params;
    } // traverseParams


    /**************************************************************************
     * traverses PARAMETER section of IN/OUTPARAMS. <BR/>
     *
     * @param parameterNode  root node of the subtree to traverse
     *
     * @return  The traversed parameter.
     */
    private ActionParameter traverseParameter (Node parameterNode)
    {
        // local variables
        String parameterValue = WorkflowConstants.UNDEFINED;
        String fieldName = null;

        // normalize the node to ensure that all text values are together
        parameterNode.normalize ();

        // get text value
        Text text = (Text) parameterNode.getFirstChild ();
        if (text != null)
        {
            parameterValue = text.getNodeValue ();
        } // if
        else
        {
            parameterValue = WorkflowConstants.UNDEFINED;
        } // else

        // get attributes of the parameter node
        NamedNodeMap attributes = parameterNode.getAttributes ();
        // get the optional NAME attribute node
        Node node = attributes.getNamedItem (WorkflowTagConstants.ATTR_FIELDNAME);
        if (node != null)
        {
            fieldName = node.getNodeValue ();
        } // if (node != null)

        return new ActionParameter (parameterValue, fieldName);
    } // traverseParameter


    /**************************************************************************
     * traverses receiver section of one state. <BR/>
     *
     * @param receiverNode  node of the subtree to traverse
     * @param aState        workflow state, represented as an object
     */
    private void traverseReceiver (Node receiverNode, State aState)
    {
        // local variables
        NamedNodeMap attributes;
        Node subnode;
        Text text;

        // get the text value
        text = (Text) receiverNode.getFirstChild ();
        if (text != null)
        {
            aState.receiver.name = text.getNodeValue ();
        } // if
        else
        {
            aState.receiver.name = WorkflowConstants.UNDEFINED;
        } // else

        // check if receiver shall be ad-hoc selected
        if (aState.receiver.name
            .equalsIgnoreCase (WorkflowConstants.RUNTIME_ADHOC))
        {
            aState.adhocReceiver = true;    // yes - state holds adhoc receiver
        } // if

        // get the attributes of the RECEIVER node
        attributes = receiverNode.getAttributes ();

        // get the DESTINATION attribute
        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_DESTINATION);
        // check if there is a DESTINATION attribute set
        if (subnode != null)
        {
            aState.receiver.destination = subnode.getNodeValue ();
        } // if

        // get the RIGHTS attribute
        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_RIGHTS);
        // check if there is a RIGHTS attribute set
        if (subnode != null)
        {
            aState.receiver.rights = subnode.getNodeValue ();
        } // if
        else
        {
            // default value
            aState.receiver.rights = WorkflowConstants.DEFAULT_RECEIVERRIGHTS;
        } // else

        // get the REMAINRIGHTS attribute
        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_REMAINRIGHTS);
        // check if there is a REMAINRIGHTS attribute set
        if (subnode != null)
        {
            aState.receiver.remainRights = subnode.getNodeValue ();
        } // if
        else
        {
            // default value
            aState.receiver.remainRights = WorkflowConstants.DEFAULT_RECEIVERREMAINRIGHTS;
        } // else

        // adhoc-state only: get additional GROUPS and USERS information
        aState.adhocGroups = WorkflowConstants.UNDEFINED;
        aState.adhocUsers = WorkflowConstants.UNDEFINED;
        if (aState.adhocReceiver)
        {
            // get the GROUPS attribute
            subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_GROUPS);
            // check if there is a GROUPS attribute set
            if (subnode != null)
            {
                aState.adhocGroups = subnode.getNodeValue ();
            } // if

            // get the USERS attribute
            subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_USERS);
            // check if there is a USERS attribute set
            if (subnode != null)
            {
                aState.adhocUsers = subnode.getNodeValue ();
            } // if
        } // if, adhoc state
    } // traverseReceiver


    /**************************************************************************
     * traverses cc section of one state. <BR/>
     *
     * @param ccNode    root node of the subtree to traverse
     * @param aState    workflow state, represented as an object
     */
    private void traverseCC (Node ccNode, State aState)
    {
        // local variables
        NamedNodeMap attributes;
        Node subnode;
        Text text;
        Receiver cc = new Receiver ();

        // normalize the node to ensure that all text values are together
        ccNode.normalize ();

        // get text value
        text = (Text) ccNode.getFirstChild ();
        if (text != null)
        {
            cc.name = text.getNodeValue ();

            // get the attributes of the CC node
            attributes = ccNode.getAttributes ();

            // get the DESTINATION attribute from the CC element
            subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_DESTINATION);
            // check if there is a DESTINATION attribute set
            if (subnode != null)
            {
                cc.destination = subnode.getNodeValue ();
            } // if

            // get the RIGHTS attribute
            subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_RIGHTS);
            // check if there is a RIGHTS attribute set
            if (subnode != null)
            {
                cc.rights = subnode.getNodeValue ();
            } // if
            else
            {
                // default value
                cc.rights = WorkflowConstants.DEFAULT_CCRIGHTS;
            } // else

            // get the REMAINRIGHTS attribute
            subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_REMAINRIGHTS);
            // check if there is a REMAINRIGHTS attribute set
            if (subnode != null)
            {
                cc.remainRights = subnode.getNodeValue ();
            } // if
            else
            {
                // default value
                cc.remainRights = WorkflowConstants.DEFAULT_CCREMAINRIGHTS;
            } // else

            // add the CC to the states CC-list
            aState.ccs.addElement (cc);
        }  // if (text != null)
    } // traverseCC


    /**************************************************************************
     * traverses a NOTIFICATION section. <BR/>
     *
     * @param notificationNode  Root node of the subtree to traverse.
     * @param aState            Workflow state, represented as an object.
     */
    private void traverseNotification (Node notificationNode, State aState)
    {
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        Notify aNotification;

        // initialize a new Notification list
        Vector<Notify> notifications = new Vector<Notify> ();

        //
        // no NOTIFICATION header information
        //

        //
        // get subnodes of NOTIFICATION
        // - NOTIFY [1..n]
        //

        // check if there are more nodes
        if (notificationNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = notificationNode.getChildNodes ();
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

                    // get the NOTIFY element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFY))
                    {
                        // create notify representation
                        aNotification = this.traverseNotify (node);

                        // add new notification to list
                        notifications.addElement (aNotification);
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // add to state
        aState.notification = notifications;
    } // traverseNotification


    /**************************************************************************
     * traverses NOTIFY section of NOTIFICATION. <BR/>
     *
     * @param   notifyNode  Root node of the subtree to traverse.
     *
     * @return  The traversed notify node.
     */
    private Notify traverseNotify (Node notifyNode)
    {
        Node node;
        NodeList nodelist;
        int size;
        Text text;
        String nodename;
        Notify notify = new Notify ();

        //
        // no NOTIFY header information
        //

        //
        // get subnodes of NOTIFY
        // - USERS
        // - SUBJECT
        // - CONTENT
        // - ACTIVITY
        // - DESCRIPTION
        //

        // check if there are more nodes
        if (notifyNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = notifyNode.getChildNodes ();
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

                    // get the USERS element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYUSERS))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.users = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYUSERS
                    // get the GROUPS element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYGROUPS))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.groups = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYUSERS
                    // get the SUBJECT element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYSUBJECT))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.subject = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYSUBJECT
                    // get the CONTENT element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYCONTENT))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.content = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYCONTENT
                    // get the ACTIVITY element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYACTIVITY))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.activity = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYACTIVITY
                    // get the DESCRIPTION element
                    if (nodename.equals (WorkflowTagConstants.ELEM_NOTIFYDESCRIPTION))
                    {
                        // normalize the node
                        node.normalize ();
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            notify.description = text.getNodeValue ();
                        } // if
                    } // if (nodename ... ELEM_NOTIFYDESCRIPTION
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (notifyNode.hasChildNodes ())

        // exit method
        return notify;
    } // traverseNotify



    /**************************************************************************
     * traverses nextstate section of one state. <BR/> This NEXSTATE is
     * of v1.0 structure. a transition of type 'SEQUENTIAL' will be created.
     *
     * @param nextStateNode  root node of the subtree to traverse
     * @param aState         workflow state, represented as an object
     */
    private void traverseV1NextState (Node nextStateNode, State aState)
    {
        Text text;

        // normalize the node to ensure that all text values are together
        nextStateNode.normalize ();

        // get text value
        text = (Text) nextStateNode.getFirstChild ();
        if (text != null)
        {
            // get name of next state
            String nextStateName = text.getNodeValue ();

            // create new transition of type sequential
            aState.transition =
                new Transition (WorkflowConstants.TRANSITIONTYPE_SEQUENTIAL);

            // add nextstate to transition (will be only transition)
            aState.transition.addTransition (nextStateName);

            // check if this is the a state and add endstate!
            // (entry can be 'END' or 'END-NOCONFIRM')
            if (nextStateName
                .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY) ||
                nextStateName
                    .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY_NOCONFIRM))
            {
                // set last state
//                 this.workflow.endStateName = aState.name;
                this.workflow.endStates.addElement (aState.name);
            } // if
        } // if
    } // traverseV1NextState



    /**************************************************************************
     * traverses message section of one state. <BR/>
     *
     * @param node      root node of the subtree to traverse
     * @param aState    workflow state, represented as an object
     */
    private void traverseMessage (Node node, State aState)
    {
        Text text;

        // normalize the node to ensure that all text values are together
        node.normalize ();
        // get text value
        text = (Text) node.getFirstChild ();
        if (text != null)
        {
            aState.message = text.getNodeValue ();
        } // if
        else
        {
            aState.message = "";
        } // else
    } // traverseMessage


    /**************************************************************************
     * traverses transition section of one state. <BR/>
     *
     * @param   transitionNode  Root node of the subtree to traverse.
     * @param   aState          Workflow state, represented as an object.
     */
    private void traverseTransition (Node transitionNode, State aState)
    {
        NamedNodeMap attributes;
        Node node;
        Node subnode;
        NodeList nodelist;
        int size;
        String nodename;
        Text text;

        // normalize the node to ensure that all text values are together
        transitionNode.normalize ();

        // get the attributes of the TRANSITION node
        attributes = transitionNode.getAttributes ();

        // get the TYPE attribute from the TRANSITION element
        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_TRANSITIONTYPE);
        // check if there is a TYPE attribute set;
        // initilialize transition object
        if (subnode != null)
        {
            aState.transition = new Transition (subnode.getNodeValue ());
        } // if
        else
        {
            aState.transition = new Transition (WorkflowConstants.UNDEFINED);
        } // else

        // check if there are more nodes
        if (transitionNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = transitionNode.getChildNodes ();
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

                    //
                    // MISSING: Distinguish different types of transitions!
                    //

                    // get the NEXTSTATE element for SEQUENTIAL transition
                    if (aState.transition.isSequential () &&
                        nodename.equals (WorkflowTagConstants.ELEM_NEXTSTATE))
                    {
                        // create variable representation
                        String nextStateName = this.traverseNextState (node);

                        // add nextstate to transition-object of state
                        aState.transition.addTransition (nextStateName);

                        // check if this is the last state and set endstate!
                        // (entry can be 'END' or 'END-NOCONFIRM')
                        if (nextStateName
                            .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY) ||
                            nextStateName
                                .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY_NOCONFIRM))
                        {
                            // add end-state
                            this.workflow.endStates.addElement (aState.name);
                        } // if
                    } // if (nodename ...
                    // get the NEXTSTATE element for ALTERNATIVE transition
                    if (aState.transition.isAlternative () &&
                        nodename.equals (WorkflowTagConstants.ELEM_NEXTSTATE))
                    {
                        // create variable representation
                        String nextStateName = this.traverseNextState (node);

                        // add nextstate to transition-object of state
                        aState.transition.addTransition (nextStateName);

                        // difference to sequential: no END-states allowed
                        if (nextStateName
                            .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY) ||
                            nextStateName
                                .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY_NOCONFIRM))
                        {
                            // log entry: no IFs allowed in non-conditional
                            // transitions
                            this.log.add (DIConstants.LOG_WARNING,
                                "Nextstate END is not allowed in non-sequential transitions - state=" +
                                nextStateName, true);
                        } // if
                    } // if (nodename ...
                    // get the IF element (for CONDITIONAL)
                    else if (nodename.equals (WorkflowTagConstants.ELEM_IF))
                    {
                        // check if transition-type is CONDITIONAL
                        if (!aState.transition.isConditional ())
                        {
                            // log entry: no IFs allowed in non-conditional transitions
                            this.log.add (DIConstants.LOG_WARNING,
                                "IF-structure is NOT allowed in non-conditional transitions - state=" +
                                aState.name, true);
                        } // if
                        else
                        {
                            // traverse if element
                            this.traverseIf (node, aState);
                        } // else
                    } // if (nodename ...
                    // get the DEFAULT element (for CONDITIONAL)
                    else if (nodename.equals (WorkflowTagConstants.ELEM_DEFAULT))
                    {
                        // check if transition-type is CONDITIONAL
                        if (!aState.transition.isConditional ())
                        {
                            // log entry: no DEFAULT allowed in non-conditional transitions
                            this.log.add (DIConstants.LOG_WARNING,
                                "DEFAULT-structure is NOT allowed in non-conditional transitions - state=" +
                                aState.name, true);
                        } // if

                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // add default branch
                            aState.transition.addTransition (text.getNodeValue ());
                        } // if
                        else
                        {
                            // add undefined branch
                            aState.transition.addTransition (WorkflowConstants.UNDEFINED);
                        } // else
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())
    } // traverseTransition


    /**************************************************************************
     * traverses if-section of one states transition. <BR/>
     *
     * @param ifNode      root node of the subtree to traverse
     * @param aState      workflow state, represented as an object
     */
    private void traverseIf (Node ifNode, State aState)
    {
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        String nextStateString = WorkflowConstants.UNDEFINED;
        Condition condition = null;
        Text text;

        // normalize the node to ensure that all text values are together
        ifNode.normalize ();

        //
        // no attributes in IF node
        //

        // only ONE condition and ONE nextstate element is allowed;
        // iterate through all elements; after loop exactly ONE
        // nextstate/condition-pair element will be added

        // check if there are more nodes
        if (ifNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = ifNode.getChildNodes ();
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

                    // get the CONDITION element
                    if (nodename.equals (WorkflowTagConstants.ELEM_CONDITION))
                    {
                        // traverse CONDITION element
                        condition = this.traverseCondition (node);
                    } // if (nodename ...
                    // get the IF element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_NEXTSTATE))
                    {
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // set element
                            nextStateString = text.getNodeValue ();
                        } // if
                        else
                        {
                            nextStateString = WorkflowConstants.UNDEFINED;
                        } // else

                        // difference to sequential: no END-states allowed
                        if (nextStateString
                            .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY) ||
                            nextStateString
                                .equalsIgnoreCase (WorkflowConstants.LASTSTATEENTRY_NOCONFIRM))
                        {
                            // log entry: no IFs allowed in non-conditional transitions
                            this.log.add (DIConstants.LOG_WARNING,
                                "Nextstate END is not allowed in non-sequential transitions - state=" +
                                aState.name, true);
                        } // if
                    } // if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // add conditional branch
        aState.transition.addTransition (nextStateString, condition);
    } // traverseIf


    /**************************************************************************
     * traverses condition-section of one states if-section. <BR/>
     *
     * @param conditionNode      root node of the subtree to traverse
     *
     * @return      condition-string-array: lhs, op, rhs
     */
    private Condition traverseCondition (Node conditionNode)
    {
        Node node;
        NodeList nodelist;
        int size;
        String nodename;
        String rhsValue;
        String operator;
        String lhsValue;
        Text text;

        // init expression
        rhsValue = operator = lhsValue = WorkflowConstants.UNDEFINED;

        // normalize the node to ensure that all text values are together
        conditionNode.normalize ();

        //
        // no attributes in IF node
        //

        // only ONE condition and ONE nextstate element is allowed;
        // iterate through all elements; after loop exactly ONE
        // nextstate/condition-pair element will be added

        // check if there are more nodes
        if (conditionNode.hasChildNodes ())
        {
            // get the child nodes
            nodelist = conditionNode.getChildNodes ();
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

                    // get the LHSVALUE element
                    if (nodename.equals (WorkflowTagConstants.ELEM_CONDITIONLHSVALUE))
                    {
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // set element
                            lhsValue = text.getNodeValue ();
                        } // if
                        else
                        {
                            lhsValue = WorkflowConstants.UNDEFINED;
                        } // else
                    } // if (nodename ...
                    // get the OPERATOR element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_CONDITIONOPERATOR))
                    {
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // set element
                            operator = text.getNodeValue ();
                        } // if
                        else
                        {
                            operator = WorkflowConstants.UNDEFINED;
                        } // else
                    } // else if (nodename ...
                    // get the RHSVALUE element
                    else if (nodename.equals (WorkflowTagConstants.ELEM_CONDITIONRHSVALUE))
                    {
                        // get text value
                        text = (Text) node.getFirstChild ();
                        if (text != null)
                        {
                            // set element
                            rhsValue = text.getNodeValue ();
                        } // if
                        else
                        {
                            rhsValue = WorkflowConstants.UNDEFINED;
                        } // else
                    } // else if (nodename ...
                } // if (node.getNodeType == Node.ElementNode)
            } // for (int i = 0; i < size; i++)
        } // if (stateNode.hasChildNodes ())

        // return new conditional element
        return new Condition (lhsValue, operator, rhsValue);
    } // traverseIf


    /**************************************************************************
     * traverses nextstate section of one transition. <BR/>
     *
     * @param nextStateNode  root node of the subtree to traverse
     *
     * @return name of nextstate element
     */
    private String traverseNextState (Node nextStateNode)
    {
        Text text;
        String nextStateName;

        // normalize the node to ensure that all text values are together
        nextStateNode.normalize ();

        // get text value
        text = (Text) nextStateNode.getFirstChild ();
        if (text != null)
        {
            // get name of next state
            nextStateName = text.getNodeValue ();
        } // if
        else
        {
            nextStateName = WorkflowConstants.UNDEFINED;
        } // else

        return nextStateName;
    } // traverseNextState


    /**************************************************************************
     * Traverse REGISTEROBSERVER section of one state. <BR/>
     *
     * @param registerNode  node of the subtree to traverse
     * @param aState        workflow state, represented as an object
     */
    private void traverseRegister (Node registerNode, State aState)
    {
        NamedNodeMap attributes;
        Node subnode;
        RegisterObserverJob job = new RegisterObserverJob ();

        // get the attributes
        attributes = registerNode.getAttributes ();

        // set unused it
        job.id = -1;

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVERJOBNAME);
        if (subnode != null)
        {
            job.name = subnode.getNodeValue ();
        } // if

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVERJOBCLASS);
        if (subnode != null)
        {
            job.className = subnode.getNodeValue ();
        } // if

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVER);
        if (subnode != null)
        {
            job.observer = subnode.getNodeValue ();
        } // if

        // add job to states register vector
        aState.registerJobs.addElement (job);
    } // traverseRegister


    /**************************************************************************
     * Traverse UNREGISTEROBSERVER section of one state. <BR/>
     *
     * @param registerNode  node of the subtree to traverse
     * @param aState        workflow state, represented as an object
     */
    private void traverseUnregister (Node registerNode, State aState)
    {
        NamedNodeMap attributes;
        Node subnode;
        RegisterObserverJob job = new RegisterObserverJob ();

        // get the attributes
        attributes = registerNode.getAttributes ();

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVERJOBID);
        if (subnode != null)
        {
            job.id = (new Integer (subnode.getNodeValue ())).intValue ();
        } // if

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVERJOBNAME);
        if (subnode != null)
        {
            job.name = subnode.getNodeValue ();
        } // if

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVERJOBCLASS);
        if (subnode != null)
        {
            job.className = subnode.getNodeValue ();
        } // if

        subnode = attributes.getNamedItem (WorkflowTagConstants.ATTR_OBSERVER);
        if (subnode != null)
        {
            job.observer = subnode.getNodeValue ();
        } // if

        // add job to states register vector
        aState.unregisterJobs.addElement (job);
    } // traverseUnregister


    /**************************************************************************
     * Checks how many true element nodes have been found in the import
     * document and returns the value found. <BR/>
     * Hint: this is time consuming but an import document can contain
     * text elements as whitespaces which corrupt the correct value. <BR/>
     *
     * @param   nodeList    ???
     * @return  The amount of objects collections found or -1 if amount could
     *          not be read.
     */
    public int checkElementsLength (NodeList nodeList)
    {
        // check if nodeList exists
        if (nodeList != null)
        {
            int size = nodeList.getLength ();
            int elementsLength = 0;
            Node node;
            // check if there are any elements
            if (size > 0)
            {
                // now go through all the objects
                for (int i = 0; i < size; i++)
                {
                    //get a node from the nodelist
                    node = nodeList.item (i);
                    // check if node is an element node
                    if (node.getNodeType () == Node.ELEMENT_NODE)
                    {
                        elementsLength++;
                    } // if (node.getNodeType () == Node.ELEMENT_NODE)
                } // for (int i = 0; i < size; i++)
            } // if (size > 0)
            return elementsLength;
        } // if (nodeList != null)

        return -1;
    } // checkElementsLength

} // class WorkflowParser

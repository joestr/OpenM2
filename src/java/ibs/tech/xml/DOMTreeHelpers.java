package ibs.tech.xml;

import ibs.app.AppMessages;
import ibs.app.UserInfo;
import ibs.bo.BOArguments;
import ibs.bo.BOConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObject;
import ibs.bo.OID;
import ibs.bo.States;
import ibs.bo.path.ObjectPathHandler;
import ibs.bo.path.ObjectPathNode;
import ibs.di.DIConstants;
import ibs.di.XMLViewer_01;
import ibs.io.Environment;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.webdav.WebdavData;
import ibs.service.conf.Configuration;
import ibs.service.workflow.WorkflowInstanceInformation;
import ibs.tech.sql.DBError;
import ibs.util.DateTimeHelpers;

import java.util.StringTokenizer;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/******************************************************************************
 * The DOMTreeHelpers class includes some useful methods that are used by other
 * object for the DOMTree creation.
 *
 * @version     $Id: DOMTreeHelpers.java,v 1.5 2010/04/07 13:37:16 rburgermann Exp $
 *
 * @author      Christa Tran (CT), 990308
 *
 * PROB KR 20090827 This class contains many classes and references from
 *              forbidden packages, like ibs.bo, etc. It should be
 *              moved to ibs.bo or ibs.di.
 ******************************************************************************
 */
public abstract class DOMTreeHelpers
{
    ///////////////////////////////////////////////////////////////////////////
    // class methods
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     *
     * @param   obj                         The business object.
     * @param   viewMode                    The specific view mode.
     * @param   isCreateSystem              Create system tags?
     * @param   isCreateSystemUser          Create system user tag?
     * @param   isCreateExtendedAttributes  Create extended attributes tags?
     * @param   isCreateValues              Create values tags?
     * @param   isCreateWorkflow            Create workflow tags?
     * @param   isCreateAttachments         Create attachment tags?
     * @param   elementName                 The element tag name.
     *
     * @return  The DOM tree of the object.
     */
    public static Node createDomTree (BusinessObject obj, int viewMode,
            boolean isCreateSystem, boolean isCreateSystemUser,
            boolean isCreateExtendedAttributes,
            boolean isCreateValues, boolean isCreateWorkflow,
            boolean isCreateAttachments, String elementName)
    {
        // check if we got an object
        if (obj == null || obj.state == States.ST_NONEXISTENT)
        {
            return null;
        } // if

        Document doc = null;
        UserInfo userInfo = null;

        Environment env = obj.getEnv ();
        ApplicationInfo app = obj.app;
        SessionInfo sess = obj.sess;

        // check if we got a session
        if (sess != null)
        {
            userInfo = (UserInfo) obj.sess.userInfo;
        } // if

        try
        {
            // create a new DOM root:
            doc = XMLWriter.createDocument ();
        } // try
        catch (XMLWriterException e)
        {
            // show error message
            IOHelpers.showMessage (e, app, sess, env, true);
            return doc;
        } // catch

        //<elementName>
        Element object = doc.createElement (elementName);
        object.setAttribute (DIConstants.ATTR_TYPECODE, obj.typeObj.getCode ());
        object.setAttribute (DIConstants.ATTR_TYPE, obj.typeName);

        // the layout name
        object.setAttribute ("LAYOUT", userInfo.userProfile.layoutName);

        // the upload path
        object.setAttribute ("UPLOADPATH", app.p_system.p_m2AbsBasePath +
            BOPathConstants.PATH_UPLOADAPPLICATIONDIR);

        // the upload url
        object.setAttribute ("UPLOADURL", sess.home +
            BOPathConstants.PATH_UPLOADAPPLICATIONDIR);

        // the current user name
        object.setAttribute ("USERNAME", userInfo.getUser ().actUsername);

        object.setAttribute ("SHOWFILESINWINDOW",
                            (userInfo.userProfile.showFilesInWindows) ?
                            MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE,
                                AppMessages.ML_MSG_BOOLTRUE, env) : 
                            MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE,
                                AppMessages.ML_MSG_BOOLFALSE, env));

        // add the head to the xml document
        doc.appendChild (object);

        // check if the system user informations shall be shown
        if (isCreateSystemUser)
        {
            // <USER>
            try
            {
                object.appendChild (doc.importNode (userInfo.getUser ().getDomTree (),
                    true));
            } // try
            catch (DOMException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, app, sess, env, true);
            } // catch
            catch (XMLWriterException e)
            {
                // should not occur, display error message:
                IOHelpers.showMessage (e, app, sess, env, true);
            } // catch
        } // if

        // check if the system informations shall be shown
        if (isCreateSystem)
        {
            //<SYSTEM>
            Element system = doc.createElement (DIConstants.ELEM_SYSTEM);
/* not used
            // set the DISPLAY attribute according the display mode
            switch (dataElement.systemDisplayMode)
            {
                case DataElement.DSP_MODE_HIDE:
                    system.setAttribute(DIConstants.ATTR_DISPLAY,
                        DIConstants.DISPLAY_NO);
                    break;
                case DataElement.DSP_MODE_BOTTOM:
                    system.setAttribute(DIConstants.ATTR_DISPLAY,
                        DIConstants.DISPLAY_BOTTOM);
                    break;
                default:
                    // nothing to do
            } // switch (displayMode)
*/
            object.appendChild (system);
            //<OID>
            Element id = doc.createElement ("OID");
            id.appendChild (doc.createTextNode (obj.oid.toString ()));
            system.appendChild (id);

            //<CONTAINER>
            Element container = doc.createElement ("CONTAINER");
            container.setAttribute ("ID", obj.containerId.toString ());
            String objPath = "";
            String pathSep = "";

            // check if the object has just been created
            // in that case we must get the path from the containerId
            // because the newly created object does not have an activated oid yet
            // resulting in an empty container string
            if (obj.state == States.ST_CREATED)
            {
                ObjectPathNode node = DOMTreeHelpers.getObjectPath (obj.containerId);
                // construct the path using the / as path separator
                while (node != null)
                {
                    objPath = node.getName () + pathSep + objPath;
                    pathSep = "/";
                    node = node.getParent ();
                } // while
            } // if
            else    // object is active
            {
                // note that getObjectPath () buffers the result
                ObjectPathNode node = DOMTreeHelpers.getObjectPath (obj.oid);
                // construct the path using the / as path separator
                while (node != null && node.getNodeType () != ObjectPathNode.TYPE_ROOT)
                {
                    objPath = node.getName () + pathSep + objPath;
                    pathSep = "/";
                    node = node.getParent ();
                } // while
            } // object is active

            container.appendChild (doc.createTextNode (objPath));
            system.appendChild (container);

            //<USERID>
            Element userId = doc.createElement ("USERID");
            userId.appendChild (doc.createTextNode (
                userInfo.getUser ().oid.toString ()));
            system.appendChild (userId);

            //<STATE>
            Element state = doc.createElement (DIConstants.ELEM_STATE);
            state.appendChild (doc.createTextNode ("" + obj.state));
            system.appendChild (state);

            //<NAME>
            Element name = doc.createElement (DIConstants.ELEM_NAME);
            name.setAttribute ("INPUT", BOArguments.ARG_NAME);
            name.appendChild (doc.createTextNode (obj.name));
            system.appendChild (name);

            //<DESCRIPTION>
            Element description = doc.createElement (DIConstants.ELEM_DESCRIPTION);
            description.setAttribute ("INPUT", BOArguments.ARG_DESCRIPTION);
            description.appendChild (doc.createTextNode (obj.description));
            system.appendChild (description);
            // ATTENTION!!
            // The line separation is only done for the VIEW and EDIT mode.
            // For the TRANSFORM mode this should not be done!
            if (viewMode == XMLViewer_01.VIEWMODE_SHOW ||
                viewMode == XMLViewer_01.VIEWMODE_EDIT)
            {
                StringTokenizer token = new StringTokenizer (obj.description, "\n");
                while (token.hasMoreElements ())
                {
                    Element line = doc.createElement (DIConstants.ELEM_LINE);
                    line.appendChild (doc.createTextNode (token.nextToken ()));
                    description.appendChild (line);
                } // while
            } // if

            //<VALIDUNTIL>
            Element validuntil = doc.createElement (DIConstants.ELEM_VALIDUNTIL);
            validuntil.setAttribute ("INPUT", BOArguments.ARG_VALIDUNTIL);
            String validDate = DateTimeHelpers.dateToString (obj.validUntil);
            validuntil.appendChild (doc.createTextNode (validDate));
            system.appendChild (validuntil);

            // <SHOWINNEWS>
            Element showInNews = doc.createElement (DIConstants.ELEM_SHOWINNEWS);
            showInNews.setAttribute ("INPUT", BOArguments.ARG_INNEWS);
            String flagText = obj.showInNews ?
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_BOOLTRUE, env) : 
                MultilingualTextProvider.getMessage(AppMessages.MSG_BUNDLE,
                    AppMessages.ML_MSG_BOOLFALSE, env);
            showInNews.appendChild (doc.createTextNode (flagText));
            system.appendChild (showInNews);

            // check if the extended system attributes informations shall be shown
            if (isCreateExtendedAttributes)
            {
                // add the extended object attributes only for the view and edit mode
                // for transformations this information is not needed.
                if (viewMode == XMLViewer_01.VIEWMODE_SHOW ||
                    viewMode == XMLViewer_01.VIEWMODE_EDIT)
                {
                    // should the extened attributes be shown
                    if (userInfo.userProfile.showExtendedAttributes)
                    {
                        system.setAttribute (DIConstants.ATTR_SHOWEXT, "1");
                    } // if
                    else
                    {
                        system.setAttribute (DIConstants.ATTR_SHOWEXT, "0");
                    } // else if

                    // <OWNER USERNAME="">
                    Element owner = doc.createElement (DIConstants.ELEM_OWNER);
                    if (obj.owner != null && obj.owner.fullname != null)
                    {
                        owner.setAttribute (DIConstants.ATTR_USERNAME, obj.owner.fullname);
                    } // if
                    system.appendChild (owner);

                    // <CREATED DATE="" USERNAME="">
                    Element creation = doc.createElement (DIConstants.ELEM_CREATED);
                    creation.setAttribute (DIConstants.ATTR_DATE,
                            DateTimeHelpers.dateTimeToString (obj.creationDate));

                    if (obj.creator != null && obj.creator.fullname != null)
                    {
                        creation.setAttribute (DIConstants.ATTR_USERNAME,
                                obj.creator.fullname);
                    } // if
                    system.appendChild (creation);

                    // <CHANGED DATE="" USERNAME="">
                    Element changed = doc.createElement (DIConstants.ELEM_CHANGED);
                    changed.setAttribute (DIConstants.ATTR_DATE,
                        DateTimeHelpers.dateTimeToString (obj.lastChanged));

                    // add the name of the changer
                    if (obj.changer != null && obj.changer.fullname != null)
                    {
                        changed.setAttribute (DIConstants.ATTR_USERNAME,
                            obj.changer.fullname);
                    } // if
                    system.appendChild (changed);

                    // if the object is check out add the checkout date and user
                    if (obj.checkedOut)
                    {
                        // <CHECKOUT DATE="" USERNAME="">
                        Element checkout = doc
                            .createElement (DIConstants.ELEM_CHECKEDOUT);
                        // add the checkout date
                        checkout.setAttribute (DIConstants.ATTR_DATE,
                            DateTimeHelpers.dateToString (obj.checkOutDate));
                        if (obj.checkOutUserName != null)
                        {
                            // add the user name
                            checkout.setAttribute (DIConstants.ATTR_USERNAME,
                                                   obj.checkOutUserName);
                        } // if
                        system.appendChild (checkout);

                        //<CHECKOUTUSERID>
                        Element checkOutUserID = doc.createElement ("CHECKOUTUSERID");
                        checkOutUserID.appendChild (doc
                            .createTextNode (obj.checkOutUserOid.toString ()));
                        system.appendChild (checkOutUserID);

                        //<CHECKOUTKEY>
                        Element checkOutKey = doc.createElement ("CHECKOUTKEY");
                        checkOutKey.appendChild (doc.createTextNode (WebdavData
                            .getDateTimeKey (obj.checkOutDate)));
                        system.appendChild (checkOutKey);

                        //<WEBDAVURL>
                        Element webdavURL = doc.createElement ("WEBDAVURL");
                        webdavURL
                            .appendChild (doc
                                .createTextNode (((Configuration) obj.app.configuration)
                                    .getWebDavURL (obj.getEnv ())));
                        system.appendChild (webdavURL);
                    } // if
                } // if
            } // if
        } // if

        // check if the value tags shall be shown
        if (isCreateValues)
        {
            // CT 20090213: Not implemented yet!:
            //<VALUES>
            // Element values = doc.createElement (DIConstants.ELEM_VALUES);
            // object.appendChild (values);
            // include values stored in the DataElement values vector:
            // DOMTreeHelpers.createDomTreeValues (obj, doc, values, viewMode);
        } // if

        // check if the workflow tags shall be shown
        if (isCreateWorkflow)
        {
            // <WORKFLOW>
            Element workflow = doc.createElement ("WORKFLOW");
            object.appendChild (workflow);
            // add the workflow information
            DOMTreeHelpers.addWorkflowInfo (obj.getWorkflowInstanceInfo (),
                workflow, viewMode);
        } // if

        // check if the attachments tags shall be shown
        if (isCreateAttachments)
        {
            // CT 20090213: Not implemented yet!:
            // <ATTACHMENTS>
            // Element attachmentNode = doc.createElement ("ATTACHMENTS");
            // object.appendChild (attachmentNode);
            // add the XMLDATA attachments
            // DOMTreeHelpers.addDomTreeAttachments (dataElement, attachmentNode, viewMode);
        } // if

        // return the DOM document
        return object;
    } // createDomTree




    /**************************************************************************
     * Create a value for the dom tree and add it directly to the tree. <BR/>
     * The parameter values should contain the already created &lt;VALUES> node.
     * This is where the value is directly added.
     *
     * @param   doc         The XML document which is used to create new nodes.
     * @param   values      The &lt;VALUES> node of the dom tree.
     * @param   fieldName   The field name for the value.
     * @param   type        The value type.
     * @param   value        The text to be added to the value. If
     *                      <CODE>null</CODE> no text is added.
     * @param   mandatory   Is the value mandatory?
     * @param   readonly    Is the value readonly?
     * @param   valueUnit   The unit of the value.
     * @param   argName     Argument name. If no argument name is defined
     *                      (<CODE>null</CODE>) the fieldName is converted into
     *                      a valid argument name. <BR/>
     *                      Otherwise the method
     *                      {@link BusinessObject#adoptArgName(String)
     *                      adoptArgName}
     *                      is executed on the argument.
     * @param   oid         The oid for the relevant object.
     * @param   viewMode    The specific view mode.
     * @param   size        Size of the value's content.
     */
    public static void createDomTreeValueNode (Document doc, Node values,
                                        String fieldName, String type,
                                        String value, String mandatory,
                                        String readonly, String valueUnit,
                                        String argName, OID oid,
                                        int viewMode, long size)
    {
        Element valueNode = null;

        // Dateiname:
        valueNode = doc.createElement (DIConstants.ELEM_VALUE);
        valueNode.setAttribute (DIConstants.ATTR_FIELD, DOMTreeHelpers
            .adoptFieldName (DOMTreeHelpers.createToken (fieldName)));
        valueNode.setAttribute (DIConstants.ATTR_TYPE, type);

        if (valueUnit != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_UNIT, valueUnit);
        } // if

        if (argName != null)
        {
            valueNode.setAttribute ("INPUT", DOMTreeHelpers.adoptArgName (argName));
        } // if
        else
        {
            valueNode.setAttribute ("INPUT",
                    DOMTreeHelpers.adoptArgName (DOMTreeHelpers.createArgument (fieldName)));
        } // else

        if (mandatory != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_MANDATORY, mandatory);
        } // if

        if (readonly != null)
        {
            valueNode.setAttribute (DIConstants.ATTR_READONLY, readonly);
        } // if

        if (value != null)
        {
            valueNode.appendChild (doc.createTextNode (value));
        } // if

        // make type-specific add-ons::
        if (type.equalsIgnoreCase (DIConstants.VTYPE_FILE))
        {
            // show name of file without OID !!!
            valueNode.setAttribute (DIConstants.ATTR_URL,
                                    oid.toString () + "/" + value);
            valueNode.setAttribute (DIConstants.ATTR_SIZE, "" + size);
        } // if

        // append the value node to the values:
        values.appendChild (valueNode);
    } // createDomTreeValueNode


    /**************************************************************************
     * Inserts the XMLDATA attachments in the DOM tree.
     *
     * @param   dataElement      The data element, which holds the data.
     * @param   attachmentNode  ???
     * @param   viewMode        ???
     */
    /*
    public static void addDomTreeAttachments(DataElement dataElement,
            Node attachmentNode, int viewMode)
    {
        // add the attachments to the dom tree
        if (dataElement.attachmentList != null &&
            !dataElement.attachmentList.isEmpty ())
        {
            Document doc = attachmentNode.getOwnerDocument ();

            for (Iterator<Node> iter = dataElement.attachmentList.iterator ();
                 iter.hasNext ();)
            {
                Document attDoc = (Document) iter.next ();

                // import the attachment in the document
                Node node = doc.importNode (attDoc.getDocumentElement (), true);
                // insert the node in the document
                attachmentNode.appendChild (node);
            } // for iter
        } // if
    } // addDomTreeAttachments
*/

    /**************************************************************************
     * Adds the workflow information to the given DOM node.
     *
     * @param   wfInfo          The WorkflowInstanceInformation.
     * @param   workflowNode    ???
     * @param   viewMode        ???
     */
    public static void addWorkflowInfo (WorkflowInstanceInformation wfInfo, Node workflowNode, int viewMode)
    {
        Document doc = workflowNode.getOwnerDocument ();

        // add the <OID> tag with the workflow oid
        Node oidNode = doc.createElement ("WFOID");
        workflowNode.appendChild (oidNode);

        // add the <WFSTATE> tag with the current state from the wf
        Node wfStateNode = doc.createElement ("WFSTATE");
        workflowNode.appendChild (wfStateNode);

        // add the <OBJSTATE> tag with the current state from the wf
        Node objStateNode = doc.createElement ("OBJSTATE");
        workflowNode.appendChild (objStateNode);

        // check if the workflowInstanceInformation is set
        if (wfInfo != null)
        {
            String wfOid = wfInfo.instanceId.toString ();
            String wfState = wfInfo.workflowState;
            String objState = wfInfo.currentState;

            // add the information
            oidNode.appendChild (doc.createTextNode (wfOid));
            wfStateNode.appendChild (doc.createTextNode (wfState));
            objStateNode.appendChild (doc.createTextNode (objState));
        } // if
    } // addWorkflowInfo


    /**************************************************************************
     * Retrieves the parent tree for a BusinessObject. <BR/>
     *
     * @param   oid     The oid of the required business object.
     *
     * @return  Leaf ObjectPathNode for this business object.
     */
    public static ObjectPathNode getObjectPath (OID oid)
    {
        ObjectPathNode objectPathNode = null;
        ObjectPathHandler treeHandler = new ObjectPathHandler ();

        try
        {
            objectPathNode = treeHandler.retrieveParentTree (oid);
        } // try
        catch (DBError e)
        {
            objectPathNode = null;
        } // catch

        return objectPathNode;
    } // getObjectPath


    /**************************************************************************
     * This method transforms a string into a valid argument that can be used
     * in a form. <BR/>
     *
     * @param   field   The namme of a field to be transformed into an argument.
     *
     * @return  A valid argument.
     */
    public static String createArgument (String field)
    {
        // replace all characters that could be critical when used in a form
        return field;
    } // ceateArgument


    /**************************************************************************
     * This method transforms a string into a valid token that can be displayed
     * in a form. <BR/>
     * Note that this method does not do any special with in the XMLViewer
     * class but is meant to be overwritten in subclasses. <BR/>
     *
     * @param   field   The namme of a field to be transformed into an argument.
     *
     * @return  A valid token to be used in a form.
     */
    public static String createToken (String field)
    {
        return field;
    } // ceateToken


    /**************************************************************************
     * This method takes a field name and possibly adopts it for the actual
     * object. <BR/>
     *
     * @param   fieldName   The name of the field.
     *
     * @return  The possibly adopted field name.
     */
    public static String adoptFieldName (String fieldName)
    {
        // return the field name unchanged:
        return fieldName;
    } // adoptFieldName


    /**************************************************************************
     * This method takes an argument name and possibly adopts it for the actual
     * object. <BR/>
     *
     * @param   argName The name of the argument.
     *
     * @return  The possibly adopted argument name.
     */
    public static String adoptArgName (String argName)
    {
        // return the argument name unchanged:
        return argName;
    } // adoptArgName


    /**************************************************************************
     * Removes beginning and ending path separators. <BR/>
     *
     * @param path      a string containing the path
     *
     * @return  the path without beginning or ending path separators.
     */
    public static final String trimPath (String path)
    {
        String pathLocal = path;        // variable for local assignments

        // delete the forward or backward slashes at the begging or end of the path
        if ((pathLocal.startsWith (BOConstants.PATH_FORWARDSEPARATOR)) ||
            (pathLocal.startsWith (BOConstants.PATH_BACKWARDSEPARATOR)))
        {
            pathLocal = pathLocal.substring (1);
        } // if ((contPath.startsWith (BOConstants.PATH_FORWARDSEPARATOR)) ||
        if (pathLocal.endsWith (BOConstants.PATH_FORWARDSEPARATOR))
        {
            pathLocal = pathLocal.substring (0, pathLocal.lastIndexOf (BOConstants.PATH_FORWARDSEPARATOR));
        } // if ((contPath.endsWith (BOConstants.PATH_FORWARDSEPARATOR)))
        else        // check for "\" ad beginning or end of path
        {
            if (pathLocal.endsWith (BOConstants.PATH_BACKWARDSEPARATOR))
            {
                pathLocal = pathLocal.substring (0, pathLocal.lastIndexOf (BOConstants.PATH_BACKWARDSEPARATOR));
            } // if
        } // check for "\" ad beginning or end of path
        return pathLocal;
    } // trimPath



    ///////////////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////////////

    /**************************************************************************
     * This constructor is just to ensure that there is no default constructor
     * generated during compilation. <BR/>
     */
    private DOMTreeHelpers ()
    {
        // nothing to do
    } // DOMTreeHelpers

} // class DOMTreeHelpers

/*
 * FilePublication_01.java
 */

// package:
package m2.version.publish;

// imports:
import ibs.bo.BOMessages;
import ibs.bo.Buttons;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIConstants;
import ibs.di.DataElement;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;

import m2.version.FileVersion_01;
import m2.version.publish.FilePVersion_01;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class represents one object for a file which can be versioned.
 * The class contains a version number. This number is gathered from
 * the VersionContainer_01 who is responsible for the versioning itself. <BR/>
 *
 * @version     $Id: FilePublication_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class FilePublication_01 extends FileVersion_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: FilePublication_01.java,v 1.10 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The attribute for the info where the object will be published. <BR/>
     */
    public static final String ATTR_ORIGINALOBJECT = "OObjekt";

    /**
     * The property contains the name of the database field of the original
     * Object from the form template.
     */
    public static final String DBFIELD_ORIGINALOBJECT = "m_oObject";


    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01. <BR/>
     */
    public FilePublication_01 ()
    {
        // call constructor of super class ObjectReference:
        super ();
    } // FilePublication_01


    /**************************************************************************
     * This constructor creates a new instance of the class FileVersion_01. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public FilePublication_01 (OID oid, User user)
    {
        // call constructor of super class ObjectReference:
        super (oid, user);
    } // FilePublication_01


    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     *
     * @param   viewMode    The specific view mode.
     *
     * @return  The DOM tree of the object.
     */
    public Document createDomTree (int viewMode)
    {
        Document doc = super.createDomTree (viewMode);
        NodeList nodeList = doc.getElementsByTagName (DIConstants.ELEM_VALUE);
        NamedNodeMap map = null;
        Node attrNode = null;
        Node attrNodeType = null;

        if (doc != null && (viewMode == XMLViewer_01.VIEWMODE_EDIT ||
            viewMode == XMLViewer_01.VIEWMODE_SHOW))
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);
                attrNodeType = map.getNamedItem (DIConstants.ATTR_TYPE);

                // check if attribute is ismaster, size or version
                if (attrNode.getNodeValue ().equalsIgnoreCase (FilePublication_01.ATTR_ORIGINALOBJECT))
                {
                    this.framesetPossible = false;
                    try
                    {
                        // remove the node
                        nodeList.item (i).getParentNode ().removeChild (nodeList.item (i));
                        i--;
                    } // try
                    catch (DOMException e)
                    {
                        this.trace ("trace ex occurred: " + e.getMessage ());
                    } // catch
                } // if
                else if (attrNodeType.getNodeValue ().equalsIgnoreCase (
                    DIConstants.VTYPE_OBJECTREF))
                {
                    this.framesetPossible = true;
                } // else if another node with type objectref
            } // for
        } // if mode is edit

        return doc;
    } // createDomTree


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
        int[] buttons = super.setInfoButtons ();

        // loop through all buttons and show only the edit button
        // if available
        for (int i = 0; i < buttons.length; i++)
        {
            if (buttons[i] == Buttons.BTN_CHECKIN ||
                buttons[i] == Buttons.BTN_CHECKINCONTAINER ||
                buttons[i] == Buttons.BTN_CHECKOUT ||
                buttons[i] == Buttons.BTN_CHECKOUTCONTAINER)
            {
                buttons[i] = Buttons.BTN_NONE;
            } // if
        } // for

        return buttons;
    } // setInfoButtons


    /**************************************************************************
     * Gets the tablename out of the actual object from the document template.
     * <BR/>
     *
     * @return  The table name.
     */
    public String getTableName ()
    {
        return this.getDocumentTemplate ().getMappingTableName ();
    } // getTableName


    /**************************************************************************
     * Overwritten method. It sets the showAsFormFrameset to false because no
     * such information is given. <BR/>
     * Then the super method is called.
     *
     * @param   representationForm  The representation form.
     * @param   function            The function value.
     *                              (necessary for rights check)
     *
     * @return  The operation succeeded successfully.
     */
    public boolean showChangeForm (int representationForm, int function)
    {
        this.showChangeFormAsFrameset = false;
        return super.showChangeForm (representationForm, function);
    } // showChangeForm


    /**************************************************************************
     * Reads the data of the object from an import element. <BR/>
     *
     * @param   dataElement The import element to read the data from.
     */
    public void readImportData (DataElement dataElement)
    {
        super.readImportData (dataElement);

        // this method is overwritten to tell the original object the oid of
        // the new created published object.
        // the oid of the original object is stored in the value ATTR_ORIGINALOBJECT

        ValueDataElement val =
            this.dataElement.getValueElement (FilePublication_01.ATTR_ORIGINALOBJECT);

        if (val != null && val.value != null)
        {
            // get the oid of the fieldref value
            String oidStr = val.value.substring (0, val.value.indexOf (','));
            if (oidStr != null)
            {
                try
                {
                    // load the original PVersion_01 object.
                    FilePVersion_01 v = (FilePVersion_01) this
                        .getObjectCache ().fetchObject (new OID (oidStr),
                            this.user, this.sess, this.env, false);
                    // set the oid of the published object
                    v.setPublishedObjectOid (this.oid);
                } // try
                catch (IncorrectOidException e)
                {
                    this.showIncorrectOidMessage (oidStr);
                } // catch
                catch (ObjectNotFoundException e)
                {
                    IOHelpers.showMessage (e.toString (),
                        this.app, this.sess, this.env);
                } // catch
                catch (TypeNotFoundException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_TYPENOTFOUND, env),
                        this.app, this.sess, this.env);
                } // catch
                catch (ObjectClassNotFoundException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_CLASSNOTFOUND, env),
                        this.app, this.sess, this.env);
                } // catch
                catch (ObjectInitializeException e)
                {
                    IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                            BOMessages.ML_MSG_INITIALIZATIONFAILED, env),
                        this.app, this.sess, this.env);
                } // catch
            } // if oidStr given
        } // if field found
        else
        {
            IOHelpers.showMessage (
                "Value field '" + FilePublication_01.ATTR_ORIGINALOBJECT + "' not found!",
                this.app, this.sess, this.env);
        } // else if field not found
    } // readImportData

} // class FilePublication_01

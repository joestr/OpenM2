/*
 * PVersion_01.java
 */

// package:
package m2.version.publish;

// import:
import ibs.bo.BOMessages;
import ibs.bo.IncorrectOidException;
import ibs.bo.OID;
import ibs.bo.ObjectClassNotFoundException;
import ibs.bo.ObjectInitializeException;
import ibs.bo.ObjectNotFoundException;
import ibs.bo.Operations;
import ibs.bo.type.TypeNotFoundException;
import ibs.di.DIConstants;
import ibs.di.ValueDataElement;
import ibs.di.XMLViewer_01;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.service.user.User;
import ibs.util.DateTimeHelpers;
import ibs.util.Helpers;
import ibs.util.NameAlreadyGivenException;
import ibs.util.NoAccessException;
import ibs.util.StringHelpers;
import ibs.util.UtilConstants;

import m2.version.Version_01;
import m2.version.publish.PublicationMessages;
import m2.version.publish.Publication_01;

import java.util.Date;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/******************************************************************************
 * This class represents one object for a file which can be versioned.
 * The class contains a version number. This number is gathered from
 * the VersionContainer who is responsible for the versioning itself. <BR/>
 *
 * @version     $Id: PVersion_01.java,v 1.14 2010/04/07 13:37:12 rburgermann Exp $
 *
 * @author      Bernd Martin (BM), 011115
 ******************************************************************************
 */
public class PVersion_01 extends Version_01
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: PVersion_01.java,v 1.14 2010/04/07 13:37:12 rburgermann Exp $";


    /**
     * The property contains the name of the form template for the property
     * pDatum. <BR/>
     */
    private static final String ATTR_PDATE  = "PDatum";

    /**
     * The property contains the name of the form template for the property
     * pObject. <BR/>
     */
    private static final String ATTR_POBJECT  = "PObjekt";

    /**
     * The property contains the name of the form template for the property
     * pObjectSize. <BR/>
     */
    private static final String ATTR_PSIZE  = "PGröße";

    /**
     * The property contains the name of the form template for the property
     * size. <BR/>
     */
    private static final String ATTR_PVERSION  = "PVersion";


    /**
     * The OID of the object which where the objects should be published. <BR/>
     */
    private OID p_publishedObjectOid = null;


    /**************************************************************************
     * This constructor creates a new instance of the class Version. <BR/>
     */
    public PVersion_01 ()
    {
        // call constructor of super class:
        super ();
    } // PVersion


    /**************************************************************************
     * This constructor creates a new instance of the class Version. <BR/>
     *
     * @param   oid     Value for the compound object id.
     * @param   user    Object representing the user.
     *
     * @deprecated KR 20090723 This constructor should not be used.
     */
    public PVersion_01 (OID oid, User user)
    {
        // call constructor of super class:
        super (oid, user);
    } // PVersion


    /**************************************************************************
     * The method returns the version of the actual object. <BR/>
     *
     * @return  The name of the object where the version is pulished in.
     */
    public String getPDate ()
    {
        // return the boolean value isMaster out of the dataelement
        if (this.dataElement.getValueElement (PVersion_01.ATTR_PDATE) == null)
        {
            return null;
        } // if
        return this.dataElement.getValueElement (PVersion_01.ATTR_PDATE).value;
    } // getPDate


    /**************************************************************************
     * The method returns the size of the actual published object. <BR/>
     *
     * @return  The size of the published object.
     */
    public String getPObjectSize ()
    {
        ValueDataElement valueElem =
            this.dataElement.getValueElement (PVersion_01.ATTR_PSIZE);
        // return the boolean value isMaster out of the dataelement
        if (valueElem == null)
        {
            return null;
        } // if

        return valueElem.value;
    } // getPObjectSize


    /**************************************************************************
     * The method returns the version of the actual object. <BR/>
     *
     * @return  The version if it is found in the dataelement or
     *          <CODE>-1</CODE> if it is not found.
     */
    public int getPVersion ()
    {
        try
        {
            ValueDataElement valueElem =
                this.dataElement.getValueElement (PVersion_01.ATTR_PVERSION);

            // return the boolean value isMaster out of the dataelement
            if (valueElem == null)
            {
                return -1;
            } // if

            return Integer.parseInt (valueElem.value);
        } // try
        catch (NumberFormatException e)
        {
            return -1;
        } // catch
    } // getPVersion


    /**************************************************************************
     * Checks if the version is published already or not. <BR/>
     *
     * @return  <CODE>true</CODE> if the version was published,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean isPublished ()
    {
        return this.getPublishedObjectOid () != null;
    } // isPublished


    /**************************************************************************
     * This method publishes the PVersion_01 object. <BR/>
     *
     * @return  <CODE>true</CODE> if the object was successfully published,
     *          <CODE>false</CODE> otherwise.
     */
    public boolean publish ()
    {
        // must be implemented if buttons shall be displayed in the container
        // and the object itself
        return false;
    } // publish


    /**************************************************************************
     * Returns the representation of the object as a DOM object. <BR/>
     * It is overwritten to prevent editing the properties which must be
     * set automatically (PObject, PVersion, PDate, PSize).
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

        if (doc != null && viewMode == XMLViewer_01.VIEWMODE_EDIT)
        {
            for (int i = 0; i < nodeList.getLength (); i++)
            {
                map = nodeList.item (i).getAttributes ();
                attrNode = map.getNamedItem (DIConstants.ATTR_FIELD);

                // check if attribute is ismaster, size or version
                if (attrNode.getNodeValue ().equalsIgnoreCase (PVersion_01.ATTR_POBJECT) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (PVersion_01.ATTR_PVERSION) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (PVersion_01.ATTR_PDATE) ||
                    attrNode.getNodeValue ().equalsIgnoreCase (PVersion_01.ATTR_PSIZE))
                {
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
            } // for
        } // if mode is edit

        return doc;
    } // createDomTree


    /**************************************************************************
     * Overwritten method. It sets the showAsFormFrameset to false when the
     * container is given already where the published objects should be stored
     * in. <BR/>
     * Then the super method is called.
     *
     * @param   representationForm  Kind of representation.
     * @param   changeFormFct       The function to be performed when submitting
     *                              the form.
     *
     * @return  The operation succeeded successfully.
     */
    /**************************************************************************
    public boolean showChangeForm (int representationForm, int function)
    {
        this.showChangeFormAsFrameset = false;

        // return the super methods return value
        return super.showChangeForm (representationForm, function);
    } // showChangeForm


    /**************************************************************************
     * . <BR/>
     *
     * @return  The oid of the published object.
     */
    public OID getPublishedObjectOid ()
    {
        if (this.p_publishedObjectOid == null)
        {
            this.setPublishedFileNameAndOid ();
        } // if not set yet

        return this.p_publishedObjectOid;
    } // getPublishedFile


    /**************************************************************************
     * This method is call from the published object after the import of the
     * transformation process to tell the 'original' object the oid of the
     * associated published object.
     *
     * @param   oid     The oid of the object which shall be set as published.
     */
    public void setPublishedObjectOid (OID oid)
    {
        try
        {
            // set the oid in the value field
            // at this point the pobject name is ignored and set to an empty string
            this.setPublishedObject (oid, "");
            // save the changes
            this.performChangeData (Operations.OP_CHANGE);
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_CHANGE);
        } // catch
        catch (NameAlreadyGivenException e)
        {
            this.showNameAlreadyGivenMessage ();
        } // catch
    } // setPublishedObjectOid


    /**************************************************************************
     * Sets the published file values in the dataelement and the local
     * properties. This method is called after the transformation process
     * (see method FilePVersion_01.publish ()) to update the info about
     * the associated published object. <BR/>
     */
    public void setPublishedObjectData ()
    {
        try
        {
            // retrieve the object datas again to reflect changes performed
            // by the method setPublishedObjectOid ().
            this.performRetrieveData (Operations.OP_READ);

            // now load the published object
            Publication_01 p = this.getPublishedObject ();
            // publication object found
            if (p != null)
            {
                // set the oid and name of the published object
                this.setPublishedObject (p.oid, p.name);
                // set the file version and size and the publication date
                (this.dataElement.getValueElement (PVersion_01.ATTR_PVERSION)).value =
                    "" + p.getVersion ();
                (this.dataElement.getValueElement (PVersion_01.ATTR_PSIZE)).value =
                    "" + p.getFilesize ();
                (this.dataElement.getValueElement (PVersion_01.ATTR_PDATE)).value =
                    DateTimeHelpers.dateToString (new Date ());

                // store the changes in the database
                this.performChangeData (Operations.OP_CHANGE);
            } // publication found
        } // try
        catch (NoAccessException e)
        {
            this.showNoAccessMessage (Operations.OP_READ);
        } // catch
        catch (NameAlreadyGivenException e)
        {
            this.showNameAlreadyGivenMessage ();
        } // catch
        catch (ObjectNotFoundException e)
        {
            this.showObjectNotFoundMessage ();
        } // catch
    } // setPublishedObjectData


    /**************************************************************************
     * Sets the published file values in the dataelement and the local
     * properties. <BR/>
     *
     * @param   oid     The oid of the published object.
     * @param   name    The name of the publihsed object.
     */
    private void setPublishedObject (OID oid, String name)
    {
        // set the fieldref in the dataelement
        (this.dataElement.getValueElement (PVersion_01.ATTR_POBJECT)).value =
            oid + "," + name;
        // set the attributes of the published file within this object
        this.p_publishedObjectOid = oid;
    } // getPublishedFile


    /**************************************************************************
     * This method sets the value for the oid and name of the object where
     * which represents the published object. <BR/>
     */
    private void setPublishedFileNameAndOid ()
    {
        String val = null;

        if (this.dataElement != null)
        {
            // get the value from the data element
            val = this.dataElement.getValueElement (PVersion_01.ATTR_POBJECT).value;
        } // if data element not null

        // if value returned is not null and valid
        if (val != null &&
            val.trim ().length () > 0 &&
            !val.trim ().equals (","))
        {
            String tmpOid = val.substring (0, val.indexOf (','));
            // return the value publishedIn value of the data element
            try
            {
                this.p_publishedObjectOid  = new OID (tmpOid);
            } // try
            catch (IncorrectOidException e)
            {
                this.showIncorrectOidMessage (tmpOid);
                Helpers.getStackTraceFromThrowable (e);
            } // catch
        } // if
    } // setPublishToContainerNameAndOid


    /**************************************************************************
     * The published object is retrieved. <BR/>
     *
     * @return  The published object.
     */
    private Publication_01 getPublishedObject ()
    {
        OID pubOid = this.getPublishedObjectOid ();
        // just need to check if published object available
        if (pubOid != null)
        {
            return this.fetchPublicationObject (pubOid);
        } // if oid is valid

        return null;
    } // getPublicationObject


    /**************************************************************************
     * Helpers method only to fetch an object from the cache. <BR/>
     *
     * @param   oid     The oid.
     *
     * @return  The publication object.
     */
    private Publication_01 fetchPublicationObject (OID oid)
    {
        try
        {
            // return the object from the cache
            return (Publication_01) this.getObjectCache ().fetchObject
                (oid, this.user, this.sess, this.env, false);
        } // try
        catch (ObjectNotFoundException e)
        {
            String oidString = null;

            if (oid != null)
            {
                oidString = oid.toString ();
            } // if

            IOHelpers.showMessage (
                StringHelpers.replace (PublicationMessages.MSG_PUBLICATIONOBJECTNOTFOUND,
                                 UtilConstants.TAG_OID, oidString),
                this.app, this.sess, this.env);
        } // catch
        catch (TypeNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_TYPENOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectClassNotFoundException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_CLASSNOTFOUND, this.env),
                    this.app, this.sess, this.env);
        } // catch
        catch (ObjectInitializeException e)
        {
            IOHelpers.showMessage (MultilingualTextProvider.getMessage (BOMessages.MSG_BUNDLE,
                    BOMessages.ML_MSG_INITIALIZATIONFAILED, this.env),
                    this.app, this.sess, this.env);
        } // catch

        return null;
    } // fetchPublicationObject

} // class PVersion_01

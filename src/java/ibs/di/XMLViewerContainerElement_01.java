/*
 * Class: XMLViewerContainerElement_01.java
 */

// package:
package ibs.di;

// imports:
//KR TODO: unsauber
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import ibs.app.AppMessages;
//KR TODO: unsauber
import ibs.app.UserInfo;
//KR TODO: unsauber
import ibs.bo.BOConstants;
import ibs.bo.BOHelpers;
import ibs.bo.BOPathConstants;
import ibs.bo.BusinessObject;
import ibs.bo.BusinessObjectInfo;
import ibs.bo.IncorrectOidException;
//KR TODO: unsauber
import ibs.bo.ContainerElement;
//KR TODO: unsauber
import ibs.bo.OID;
import ibs.di.DataElement;
import ibs.di.DIConstants;
import ibs.di.DIHelpers;
import ibs.di.ValueDataElement;
//KR TODO: unsauber
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.io.session.ApplicationInfo;
import ibs.io.session.SessionInfo;
import ibs.ml.MultilingualTextProvider;
//KR TODO: unsauber
import ibs.service.user.User;
import ibs.tech.html.BlankElement;
import ibs.tech.html.FormElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.InputElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.UtilConstants;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * XMLViewerContainerElement. <BR/>
 *
 * @version     $Id: XMLViewerContainerElement_01.java,v 1.35 2010/04/07 13:37:05 rburgermann Exp $
 *
 * @author      Bernd Buchegger (BB), 990413
 ******************************************************************************
 */
public class XMLViewerContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: XMLViewerContainerElement_01.java,v 1.35 2010/04/07 13:37:05 rburgermann Exp $";


    /**
     * Flag to use the standard headers in the list. <BR/>
     */
    public User user;

    /**
     * Environment for getting input and generating output. <BR/>
     */
    public Environment env = null;

    /**
     * Flag to use the standard headers in the list. <BR/>
     */
    public boolean useStandardHeader = true;

    /**
     * alternative fields to use in the header. <BR/>
     */
    public String[] headerFieldsArray = null;

    /**
     * array of boolean values to mark the system fields in the header. <BR/>
     */
    public boolean[] headerIsSysFieldsArray = null;

    /**
     * the dataElement stores the data. <BR/>
     */
    public DataElement dataElement = null;

    /**
     * Holds the actual session info. <BR/>
     */
    private SessionInfo p_sess = null;

    /**
     * Holds the actual application info. <BR/>
     */
    private ApplicationInfo p_app = null;

    /**
     * Flag to show value domains as object links. <BR/>
     * Per default this as been set to false since we do not need this
     * behaviour.<BR/>
     */
    public boolean p_showVDAsLink = false;    
    
    
    /**************************************************************************
     * Creates a XMLViewerContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public XMLViewerContainerElement_01 ()
    {
        // call constructor of super class:
        super ();

        // initialize the instance's public properties:
    } // ReferenzContainerObject_01


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid         Value for the compound object id.
     */
    public XMLViewerContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:

    } // SendObjectContainerElement_01_01


    /**************************************************************************
     * Set session info object. <BR/>
     *
     * @param   sess    The session info object to be set.
     */
    public void setSessionInfo (SessionInfo sess)
    {
        // set the property value:
        this.p_sess = sess;
    } // setSessionInfo


    /**************************************************************************
     * Get session info object. <BR/>
     *
     * @return  The session info object.
     */
    public SessionInfo getSessionInfo ()
    {
        // get the property value and return it:
        return this.p_sess;
    } // getSessionInfo


    /**************************************************************************
     * Set application info object. <BR/>
     *
     * @param   app     The application info object to be set.
     */
    public void setApplicationInfo (ApplicationInfo app)
    {
        // set the property value:
        this.p_app = app;
    } // setApplicationInfo


    /**************************************************************************
     * Get application info object. <BR/>
     *
     * @return  The application info object.
     */
    public ApplicationInfo getApplicationInfo ()
    {
        // get the property value and return it:
        return this.p_app;
    } // getApplicationInfo


    /**************************************************************************
     * Constructs the path for image files. <BR/>
     *
     * @return the file path for the XMLViewer XML file
     */
    protected String getImagePath ()
    {
        // create the filepath
        String m2AbsBasePath = this.p_sess.home;
        return m2AbsBasePath + BOPathConstants.PATH_UPLOAD_ABS_FILES +
                         this.oid + "/";
/* KR 20060120 replaced by standard upload path
        return m2AbsBasePath + BOPathConstants.PATH_UPLOAD_ABS_IMAGES;
*/
    } // getImagePath


    /**************************************************************************
     * Represent this object to the user. <BR/>
     *
     * @param   classId     The CSS class to be set for the actual element.
     * @param   env         The current environment
     *
     * @return  The constructed table row element.
     */
    public RowElement show (String classId, Environment env)
    {
        GroupElement gel;
        ValueDataElement vel;
        String valueStr;
        String path;
        LinkElement linkElem;
        RowElement tr;
        TableDataElement td;
        boolean isSysField = false;

        // In case you don't want to show the standard headers
        // we have to deactivate the showExtendedAttributes flag
        // in order to suppress the display of 'typename', 'created at'
        // and the 'creator' fields.
        if (!this.useStandardHeader)
        {
            this.showExtendedAttributes = false;
        } // if

        tr =  super.show (classId, env);

        // check whether to show extended headers
        if (!this.useStandardHeader &&  this.headerFieldsArray != null &&
            this.headerFieldsArray.length > 0)
        {
            for (int i = 0; i < this.headerFieldsArray.length; i++)
            {
                gel = new GroupElement ();
                // there must be a dataelement
                if (this.dataElement != null)
                {
                    isSysField = false;
                    // do we have the sysfields array?
                    if (this.headerIsSysFieldsArray != null)
                    {
                        isSysField = this.headerIsSysFieldsArray[i];
                    } // if

                    // do we have a system field?
                    if (isSysField)
                    {
                        // display the value for the system field
                        gel.addElement (new TextElement (
                            DIHelpers.getSysFieldValue (this.headerFieldsArray[i], this)));
                    } // if (isSysField)
                    else // standard field handling
                    {
                        // get the value to the associated header field
                        vel = this.dataElement.getValueElement (this.headerFieldsArray[i]);
                        // check if we could find the valuedataElement object
                        // and if it has a value. in case it has no value
                        // just display a blank element
                        if (vel != null && vel.value != null && vel.value.length () > 0)
                        {
                            boolean multiSelection = vel.multiSelection != null && vel.multiSelection.equalsIgnoreCase (DIConstants.ATTRVAL_YES);

                            // differentiate types
                            if (vel.type.equals (DIConstants.VTYPE_OPTION))
                            {
                                // get the active value of the option field
                                gel.addElement (new TextElement (DIHelpers.getFirstToken (vel.value, DIConstants.OPTION_DELIMITER)));
                            } // if (vel.type.equals (DIConstants.VTYPE_OPTION))
                            else if (vel.type.equals (DIConstants.VTYPE_BOOLEAN))
                            {
                                // get the active value of the option field
                                if (vel.value != null &&  DIConstants.BOOL_TRUE_VALUES.indexOf (vel.value.toUpperCase ()) != -1)

                                {
                                    gel.addElement (new TextElement ( 
                                        MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                            AppMessages.ML_MSG_BOOLTRUE, env)));
                                } // if (vel.value.toUpperCase().indexOf(DIConstants.BOOL_TRUE_VALUES) >= 0)
                                else
                                {
                                    gel.addElement (new TextElement ( 
                                        MultilingualTextProvider.getMessage (AppMessages.MSG_BUNDLE,
                                            AppMessages.ML_MSG_BOOLFALSE, env)));
                                } //else display no
                            } // else if (vel.type.equals (DIConstants.VTYPE_BOOLEAN))
                            else if (vel.type.equals (DIConstants.VTYPE_EMAIL))
                            {
                                // display as mail link
                                gel.addElement (new LinkElement (
                                    new TextElement (vel.value),
                                    "mailto:" + vel.value));
                            } // else if (vel.type.equals (DIConstants.VTYPE_EMAIL))
                            else if (vel.type.equals (DIConstants.VTYPE_URL))
                            {
                                // display as hyperlink
                                String masterUrl = "";
                                String tempurl = "";
                                if (vel.value.indexOf (":") == -1)
                                {
                                    tempurl = IOConstants.URL_HTTP + vel.value;
                                } // if
                                else
                                {
                                    tempurl = vel.value;
                                } // else
                                // show link in window or in
                                if (((UserInfo) this.p_sess.userInfo).userProfile.showFilesInWindows)
                                {
                                    masterUrl = IOConstants.URL_JAVASCRIPT +
                                        "top.loadWindowLink (\'" + tempurl + "\');";
                                } // if
                                else
                                {
                                    masterUrl = IOConstants.URL_JAVASCRIPT +
                                        "top.loadFile (\'" + tempurl + "\');";
                                } // else
                                gel.addElement (new LinkElement (new TextElement (vel.value), masterUrl));
                            } // else if (vel.type.equals (DIConstants.VTYPE_URL))
                            else if (vel.type.equals (DIConstants.VTYPE_FILE))
                            {
                                // display as link to file
//                                path = this.p_sess.home + BOPathConstants.PATH_UPLOAD_FILES + this.oid.toString() + "/";
                                path = this.oid.toString () + "/";
                                valueStr = vel.value;
                                // check if the filename contains an oid:
                                if (valueStr.length () >= 18 &&
                                    valueStr.startsWith (UtilConstants.NUM_START_HEX))
                                    // oid contained?
                                {
/*
                                    try
                                    {
                                        OID validOid = new OID (valueStr.substring (0, 18));
                                    } // try
                                    catch (IncorrectOidException e)
                                    {
                                        // do nothing, filename is shown as it is
                                    } // catch
*/
                                    valueStr = valueStr.substring (18);
                                } // if oid contained

                                // show in window or in frameset?
                                if (((UserInfo) this.p_sess.userInfo).userProfile.showFilesInWindows)
                                {
                                    linkElem = new LinkElement (new TextElement (valueStr),
                                        IOConstants.URL_JAVASCRIPT +
                                        "top.loadWindowFile ('" + path + vel.value + "', '" + valueStr + "', null, null, null, true)");
                                } // if
                                else
                                {
                                    linkElem = new LinkElement (new TextElement (valueStr),
                                        IOConstants.URL_JAVASCRIPT +
                                        "top.loadFile ('" + path + vel.value + "', null, true);");
                                } // else
                                gel.addElement (linkElem);
                            } // else if (vel.type.equals (DIConstants.VTYPE_FILE))
                            else if (vel.type.equals (DIConstants.VTYPE_IMAGE))
                            {
                                // display an image
                                path = this.getImagePath ();
                                linkElem = new LinkElement (new ImageElement (path + vel.value),
                                    IOHelpers.getShowObjectJavaScriptUrl ("" + this.oid));
                                gel.addElement (linkElem);
                            } // else if (vel.type.equals (DIConstants.VTYPE_FILE))
                            else if (vel.type.equalsIgnoreCase (DIConstants.VTYPE_BUTTON))
                            {
                                // create a new GroupElement to show the button
                                GroupElement gel2 = new GroupElement ();

                                // get the label of the button -- the rest is the script
                                String label = "";
                                String script = "";
                                int index = 0;

                                // get the index of the delimiter
                                index = vel.value.indexOf (DIConstants.OPTION_DELIMITER);

                                // if an delimiter was found, break the string
                                if (index > 0)
                                {
                                    label = vel.value.substring (0, index);
                                    script = vel.value.substring (index + 1);
                                } // if (index > 0)

                                // add a button
                                InputElement button = new InputElement (vel.field, InputElement.INP_BUTTON, label);
                                button.onClick = script;
                                FormElement buttonForm = new FormElement ("", "");
                                buttonForm.addElement (button);
                                gel2.addElement (buttonForm);
                                gel.addElement (gel2);
                            } // else if (vel.type.equalsIgnoreCase(DIConstants.VTYPE_BUTTON))
                            else if (vel.type.equalsIgnoreCase (DIConstants.VTYPE_OBJECTREF) ||
                                    vel.type.equalsIgnoreCase (DIConstants.VTYPE_FIELDREF) ||
                                    (vel.type.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN) && !multiSelection))
                            {
                                OID refObjOid = null;
                                String refObjName = "";
                                if ((vel.value != null) && (vel.value.length () > 0))
                                {
                                    int index = 0;

                                    // get the index of the delimiter
                                    index = vel.value.indexOf (DIConstants.OPTION_DELIMITER);
                                    // if an delimiter was found, break the string
                                    if (index > 0)
                                    {
                                        try
                                        {
                                            refObjOid = new OID (vel.value.substring (0, index));
                                        } // try
                                        catch (IncorrectOidException e)
                                        {
                                            // cannot be
                                        } // catch
                                        refObjName = vel.value.substring (index + 1);
                                    } // if (index > 0)
                                    else
                                    {
                                        try
                                        {
                                            refObjOid = new OID (vel.value);

                                            if (!refObjOid.isEmpty ())
                                            {
	                                            // get the name of the object:
                                            	BusinessObjectInfo refObjInfo =
                                            		BOHelpers.findObjects (null, null, refObjOid.toStringBuilderQu (), null, this.env).firstElement ();
	                                            // check if we found the object data element:
	                                            if (refObjInfo != null)
	                                            {
	                                            	// get the relevant data out of the data element:
	                                                refObjName = refObjInfo.getName ();
	                                            } // if
                                            } // if
                                        } // try
                                        catch (IncorrectOidException e)
                                        {
                                            // cannot be
                                        } // catch
                                        catch (NoSuchElementException e)
                                        {
                                        	// referenced object not found, nothing to do
                                        } // catch
                                        catch (NullPointerException e)
                                        {
                                        	// referenced object not found, nothing to do
                                        } // catch
                                    } // else
                                } // if ((vie.value != null) && (vie.value.length () == 0))

                                if (refObjOid == null)
                                {
                                    refObjOid = OID.getEmptyOid ();
                                } // if (refObjOid == null)
                                
                                // shall a value domain be shown as link?
                                if (vel.type.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN) && 
                                	!p_showVDAsLink)
                                {
                                    gel.addElement (new TextElement (refObjName));
                                } // if (vel.type.equalsIgnoreCase (DIConstants.VTYPE_VALUEDOMAIN) ...
                                else // show an object link
                                {
                                    gel.addElement (new LinkElement (new TextElement (refObjName),
                                            IOHelpers.getShowObjectJavaScriptUrl ("" + refObjOid)));                                	
                                } // else show an object link
                                
                            } // else if (vel.type.equalsIgnoreCase(DIConstants.VTYPE_OBJECTREF) ||
                              //          vel.type.equalsIgnoreCase(DIConstants.VTYPE_FIELDREF)  ||
                              //          (vel.type.equalsIgnoreCase(DIConstants.VTYPE_VALUEDOMAIN) && !multiSelection))
                            else if (vel.type.startsWith (DIConstants.VTYPE_VALUEDOMAIN) && multiSelection)
                            {
                                StringTokenizer tok = new StringTokenizer (
                                    vel.value,
                                    BOConstants.MULTISELECTION_VALUE_SAPERATOR);

                                while (tok.hasMoreTokens ())
                                {
                                    try
                                    {
                                        OID refOid = new OID (tok.nextToken ());

                                        // check if the oid is valid:
                                        if (!refOid.isEmptyInDomain ())     // oid is valid?
                                        {
                                            // try to get object via oid:
                                            BusinessObject refObj =
                                                BOHelpers.getObject (
                                                    refOid, this.env, false,
                                                    false, false);

                                            // get first FIELDS element in referenced object field VALUE
                                            ReferencedObjectInfo fri = (vel.p_subTags != null) ? (ReferencedObjectInfo) vel.p_subTags
                                                    .elementAt (0) : null;

                                            // get the defined value from the found object
                                            String value = DIHelpers.getSysFieldValue (fri.getName (), refObj);

                                            // shall the value data element be
                                            // shown as link?
                                            if (p_showVDAsLink)
                                            {
                                                gel.addElement (new LinkElement (
                                                		new TextElement (value),
                                                        IOHelpers.getShowObjectJavaScriptUrl ("" + refOid.toString ())));
                                            	
                                            } // if (p_showVDasLink)
                                            else // show the value domain as pure text 
                                            {
                                                gel.addElement (new TextElement (value));                                            	
                                            } // else // show the value domain as pure text                                            	

                                            if (tok.hasMoreTokens ())
                                            {
                                                gel.addElement (new TextElement (
                                                        IOHelpers.MULTISELECTION_DISPLAY_SAPERATOR));
                                            } // hasMoreTokens

                                        } // if oid is valid
                                    } // try
                                    catch (IncorrectOidException ex)
                                    {
                                        //Ignore this oid
                                    } // catch

                                } // while has more tokens
                            } // else if (vel.type.startsWith(DIConstants.VTYPE_VALUEDOMAIN) && multiSelection)
                            else if (vel.type.equals (DIConstants.VTYPE_SEPARATOR))
                            {
                                // display an image
                                gel.addElement (new BlankElement ());
                            } // else if (vel.type.equals (DIConstants.VTYPE_FILE))
                            else if (multiSelection &&
                                (vel.type
                                    .equals (DIConstants.VTYPE_SELECTIONBOX) || vel.type
                                    .equals (DIConstants.VTYPE_QUERYSELECTIONBOX)))
                            {
                                String value = vel.value;

                                value = value.replaceAll ("\\" +
                                    BOConstants.MULTISELECTION_VALUE_SAPERATOR,
                                    IOHelpers.MULTISELECTION_DISPLAY_SAPERATOR);

                                gel.addElement (new TextElement (value));
                            } // else if (vel.type.equals(DIConstants.VTYPE_SELECTION) || vel.type.equals(DIConstants.VTYPE_QUERYSELECTIONBOX))
                            else // display as normal text
                            {
                                gel.addElement (new TextElement (vel.value));
                            } // else display as normal text
                        } // if (vel != null)
                        else
                        {
                            gel.addElement (new BlankElement ());
                        } // else (vel == null)
                    } // else // standard field handling
                } // if (this.dataElement != null)
                else
                {   // display a blank element
                    gel.addElement (new BlankElement ());
                } // else display a blank
                td = new TableDataElement (gel);
                td.classId = classId;
                tr.addElement (td);
            } // for (int i = 0; i < this.headerFieldsArray.length; i++)
        } // if (this.dataElement != null && !useStandardHeader && ...
        return tr;
    } // show

} // class XMLViewerContainerElement_01

/*
 * Class: AttachmentContainerElement_01.java
 */

// package:
package ibs.obj.doc;

// imports:
import ibs.bo.BOListConstants;
import ibs.bo.BOPathConstants;
import ibs.bo.BOTokens;
import ibs.bo.ContainerElement;
import ibs.bo.OID;
import ibs.io.Environment;
import ibs.io.IOConstants;
import ibs.io.IOHelpers;
import ibs.ml.MultilingualTextProvider;
import ibs.obj.doc.DocConstants;
import ibs.tech.html.BlankElement;
import ibs.tech.html.GroupElement;
import ibs.tech.html.ImageElement;
import ibs.tech.html.LinkElement;
import ibs.tech.html.RowElement;
import ibs.tech.html.TableDataElement;
import ibs.tech.html.TextElement;
import ibs.util.DateTimeHelpers;


/******************************************************************************
 * This class represents all necessary properties of one element of a
 * ReferenzContainer. <BR/>
 *
 * @version     $Id: AttachmentContainerElement_01.java,v 1.17 2010/11/24 15:10:17 btatzmann Exp $
 *
 * @author      Stampfer Heinz Josef (HJ), 980602
 ******************************************************************************
 */
public class AttachmentContainerElement_01 extends ContainerElement
{
    /**
     * Version info of the actual class. <BR/>
     * This String contains the version number, date, and author of the last
     * check in to the code versioning system. This is implemented as CVS tag
     * to ensure that it is automatically updated by the cvs system.
     */
    public static final String VERSIONINFO =
        "$Id: AttachmentContainerElement_01.java,v 1.17 2010/11/24 15:10:17 btatzmann Exp $";


    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public String  sourceName;

    /**
     * The virtual path to the source file - upload dir. <BR/>
     */
    public String  path;

    /**
     * The Name of the sended BusinesObject. <BR/>
     */
    public int  attachmentType;

    /**
     * Store if a attachment is a Master or not. <BR/>
     */
    public int  isMaster;

    /**
     * The String directed to a attachmentType. <BR/>
     */
    public String  attachmentString = " Verweis";

    /**
     * The size of a file. <BR/>
     */
    public float filesize = 0;


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     */
    public AttachmentContainerElement_01 ()
    {
        // call constructor of super class:
        super ();
        // initialize the instance's public properties:
        this.sourceName = "default.txt";

    } // AttachmentContainerElement_01


    /**************************************************************************
     * Creates a SendObjectContainerElement_01 object. <BR/>
     * This constructor calls the constructor of the super class. <BR/>
     *
     * @param   oid     Value for the compound object id.
     */
    public AttachmentContainerElement_01 (OID oid)
    {
        // call constructor of super class:
        super (oid);

        // initialize the instance's public properties:
        this.sourceName = "default.txt";
    } // AttachmentContainerElement_01


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
        String masterUrl = null;        // contains objectpath for IE4
        String url = null;              // contains the link path
        TextElement text = null;
        RowElement tr;

        if (this.showExtendedAttributes)
        {
            tr = new RowElement (
                BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINER.length + 1);
        } // if
        else
        {
            tr = new RowElement (
                BOListConstants.LST_HEADINGS_ATTACHMENTCONTAINERREDUCED.length + 1);
        } // else

        tr.classId = classId;

        TableDataElement td = null;
        if (this.isNew)
        {
            td = new TableDataElement (new ImageElement (
                BOPathConstants.PATH_GLOBAL + "new.gif"));
        } // if
        else
        {
            td = new TableDataElement (new BlankElement ());
        } // else
        td.width = BOListConstants.LST_NEWCOLWIDTH;
        td.classId = classId;

        tr.addElement (td);

        if (this.icon == null)          // no icon provided?
        {
            this.icon = this.typeName + ".gif"; // get icon from type
        } // if

        ImageElement img = new ImageElement (this.layoutpath +
            BOPathConstants.PATH_OBJECTICONS + this.icon);
        GroupElement nameGroup = new GroupElement ();
        nameGroup.addElement (img);
        nameGroup.addElement (new BlankElement ());

        if (this.isLink)                // object is a link to another object?
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
            nameGroup.addElement (img);
        } // if
        text = new TextElement (this.name);
        nameGroup.addElement (text);
        td = new TableDataElement (new LinkElement (nameGroup,
            IOHelpers.getShowObjectJavaScriptUrl (this.oid.toString ())));

        td.classId = classId;
        tr.addElement (td);

        if (this.isMaster == 1)
        {
            img = new ImageElement (this.layoutpath +
                BOPathConstants.PATH_OBJECTICONS + "Master.gif");
            nameGroup.addElement (img);
        } // if

        if (this.showExtendedAttributes)
        {
            GroupElement typeGroup = new GroupElement ();

            if (this.isLink)            // object is a link?
            {
                img = new ImageElement (this.layoutpath +
                    BOPathConstants.PATH_OBJECTICONS + "Referenz.gif");
                typeGroup.addElement (img);
                text = new TextElement (this.typeName);
                typeGroup.addElement (text);
            } // if object is a link?
            else                            // object is not a link
            {

                if (this.attachmentType == DocConstants.ATT_FILE)
                {
                    this.attachmentString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_FILE, env);
                } // if
                if (this.attachmentType == DocConstants.ATT_HYPERLINK)
                {
                    this.attachmentString = MultilingualTextProvider.getText (BOTokens.TOK_BUNDLE, BOTokens.ML_HYPERLINK, env);
                } // if
                text = new TextElement (this.attachmentString);
                typeGroup.addElement (text);
            } // else object is not a link

            td = new TableDataElement (typeGroup);

            td.classId = classId;
            tr.addElement (td);
        } // if

        GroupElement contGroup = new GroupElement ();
        LinkElement sourceNameLink;
        LinkElement dwlLink;
        ImageElement dwlimg;
        text = new TextElement (this.sourceName);

        if (this.attachmentType == DocConstants.ATT_FILE)
        {
            // BT 20101024 - IBS-595 FileNotFoundException when using file link within attachment container
            String pathLocal = this.path;

            if (pathLocal.length () > 0)
            {
                // note that the path has the format
                // /<appdir>/upload/files/<oid>/
                pathLocal = pathLocal.substring (pathLocal.lastIndexOf ("/", pathLocal.length () - 2));
            } // if
            // BT 20101024 - END

            if (this.showInWindow)
            {
                masterUrl = IOConstants.URL_JAVASCRIPT +
                    "top.loadWindowFile (\'" + this.path + this.sourceName +
                    "\','" + this.sourceName + "', null, null, null, true);";
            } // if (showInWindow)
            else
            {
                masterUrl = IOConstants.URL_JAVASCRIPT + "top.loadFile (\'" +
                    this.path + this.sourceName + "\', null, true);";
            } // else (showInWindow)

        } //if (attachmentType == DocConstants.ATT_FILE)
        else                            // Attachment is a link
        {
            if (this.sourceName == null)
            {
                this.sourceName = "";
            } // if (sourceName == null)

            if (this.sourceName.indexOf (":") == -1)
            {
                url = IOConstants.URL_HTTP + this.sourceName;
            } // if
            else
            {
                url = this.sourceName;
            } // else

            if (this.showInWindow)
            {
                masterUrl = IOConstants.URL_JAVASCRIPT +
                    "top.loadWindowLink (\'" + url + "\');";
            } // if (showInWindow)
            else
            {
                masterUrl = IOConstants.URL_JAVASCRIPT +
                    "top.loadFile (\'" + url + "\');";
            } // else (showInWindow)
        } // else (attachmentType == DocConstants.ATT_FILE)

        sourceNameLink = new LinkElement (text, masterUrl);

        contGroup.addElement (sourceNameLink);

        // check if this is a file to know if the download button shall be
        // displayed:
        if (this.attachmentType == DocConstants.ATT_FILE) // file?
        {
            // Checking if filename <> null or ""
            // to activate display of downloadbutton
            String filename = new String ();
            filename = this.sourceName.trim ();

            if (filename != null && filename.length () != 0)
            {
                contGroup.addElement (new BlankElement ());
                dwlimg = new ImageElement (this.layoutpath +
                    BOPathConstants.PATH_OBJECTICONS + "Download.gif");
                dwlLink = new LinkElement (dwlimg, this.path + this.sourceName);
                contGroup.addElement (dwlLink);
            } // if
        } // if file

        td = new TableDataElement (contGroup);

        td.classId = classId;
        tr.addElement (td);

        String str = null;
        str = ibs.util.Helpers.convertFileSize (this.filesize, env);
        text = new TextElement (str);
        td = new TableDataElement (text);

        td.classId = classId;
        tr.addElement (td);

        if (this.showExtendedAttributes)
        {
            text = new TextElement (DateTimeHelpers.dateTimeToString (this.lastChanged));
            td = new TableDataElement (text);
            td.classId = classId;
            td.alignment = IOConstants.ALIGN_RIGHT;
            tr.addElement (td);
        } // if

        return tr;                      // return the constructed row
    } // show

} // class AttachmentContainerElement_01
